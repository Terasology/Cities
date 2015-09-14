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

import java.math.RoundingMode;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.terasology.cities.common.Edges;
import org.terasology.cities.parcels.Parcel;
import org.terasology.cities.parcels.ParcelFacet;
import org.terasology.cities.sites.Site;
import org.terasology.commonworld.Orientation;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.BaseVector2f;
import org.terasology.math.geom.LineSegment;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.Requires;
import org.terasology.world.generation.facets.SurfaceHeightFacet;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Produces a {@link BuildingFacet}.
 */
@Produces(BuildingFacet.class)
@Requires({@Facet(ParcelFacet.class), @Facet(SurfaceHeightFacet.class)})
public class BuildingFacetProvider implements FacetProvider {

    private final LoadingCache<Parcel, Set<Building>> cache = CacheBuilder.newBuilder().build(
            new CacheLoader<Parcel, Set<Building>>() {

        @Override
        public Set<Building> load(Parcel parcel) {
            return generateBuildings(parcel);
        }
    });

    @Override
    public void process(GeneratingRegion region) {

        Border3D border = region.getBorderForFacet(BuildingFacet.class);
        BuildingFacet facet = new BuildingFacet(region.getRegion(), border);

        ParcelFacet parcelFacet = region.getRegionFacet(ParcelFacet.class);

        for (Parcel parcel : parcelFacet.getParcels()) {
            Set<Building> bldgs = cache.getUnchecked(parcel);
            for (Building bldg : bldgs) {
                facet.addBuilding(bldg);
            }
        }

        region.setRegionFacet(BuildingFacet.class, facet);
    }

    private Set<Building> generateBuildings(Parcel parcel) {

        DefaultBuilding b = new DefaultBuilding(parcel.getOrientation());
        Rect2i layout = new Rect2i(parcel.getShape());
        layout.expand(new Vector2i(-2, -2));

        b.addPart(new RectBuildingPart(layout));

        Rect2i fenceRc = new Rect2i(parcel.getShape());
        Orientation gateOrient = parcel.getOrientation();
        LineSegment seg = Edges.getEdge(fenceRc, parcel.getOrientation());
        Vector2i gatePos = new Vector2i(BaseVector2f.lerp(seg.getStart(), seg.getEnd(), 0.5f), RoundingMode.HALF_UP);

        return Collections.singleton(b);
    }

}
