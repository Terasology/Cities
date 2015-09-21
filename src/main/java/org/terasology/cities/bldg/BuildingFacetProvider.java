/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.cities.bldg;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cities.bldg.gen.BuildingGenerator;
import org.terasology.cities.bldg.gen.DefaultBuildingGenerator;
import org.terasology.cities.door.Door;
import org.terasology.cities.door.DoorFacet;
import org.terasology.cities.parcels.Parcel;
import org.terasology.cities.parcels.ParcelFacet;
import org.terasology.cities.surface.InfiniteSurfaceHeightFacet;
import org.terasology.cities.window.Window;
import org.terasology.cities.window.WindowFacet;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.Requires;
import org.terasology.world.generation.Updates;
import org.terasology.world.generation.facets.SurfaceHeightFacet;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Produces a {@link BuildingFacet}.
 */
@Produces(BuildingFacet.class)
@Updates({@Facet(WindowFacet.class), @Facet(DoorFacet.class)})
@Requires({@Facet(ParcelFacet.class), @Facet(SurfaceHeightFacet.class)})
public class BuildingFacetProvider implements FacetProvider {

    private static final Logger logger = LoggerFactory.getLogger(BuildingFacetProvider.class);

    private final Cache<Parcel, Set<Building>> cache = CacheBuilder.newBuilder().build();

    private BuildingGenerator bldgGenerator;

    @Override
    public void setSeed(long seed) {
        bldgGenerator = new DefaultBuildingGenerator(seed);
    }

    @Override
    public void process(GeneratingRegion region) {

        Border3D border = region.getBorderForFacet(BuildingFacet.class);
        BuildingFacet facet = new BuildingFacet(region.getRegion(), border);

        ParcelFacet parcelFacet = region.getRegionFacet(ParcelFacet.class);
        InfiniteSurfaceHeightFacet heightFacet = region.getRegionFacet(InfiniteSurfaceHeightFacet.class);

        WindowFacet windowFacet = region.getRegionFacet(WindowFacet.class);
        DoorFacet doorFacet = region.getRegionFacet(DoorFacet.class);

        for (Parcel parcel : parcelFacet.getParcels()) {
            Set<Building> bldgs;
            try {
                bldgs = cache.get(parcel, () -> bldgGenerator.generate(parcel, heightFacet));
                for (Building bldg : bldgs) {
                    facet.addBuilding(bldg);

                    // TODO: add bounds check
                    for (BuildingPart part : bldg.getParts()) {
                        for (Window wnd : part.getWindows()) {
                            windowFacet.addWindow(wnd);
                        }
                        for (Door door : part.getDoors()) {
                            doorFacet.addDoor(door);
                        }
                    }
                }
            } catch (ExecutionException e) {
                logger.error("Could not compute buildings for {}", region.getRegion(), e);
            }
        }

        region.setRegionFacet(BuildingFacet.class, facet);
    }
}
