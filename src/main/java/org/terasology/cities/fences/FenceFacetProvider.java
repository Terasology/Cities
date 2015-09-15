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

package org.terasology.cities.fences;

import java.math.RoundingMode;
import java.util.Optional;

import org.terasology.cities.common.Edges;
import org.terasology.cities.parcels.Parcel;
import org.terasology.cities.parcels.ParcelFacet;
import org.terasology.commonworld.Orientation;
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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Produces an empty {@link FenceFacet}.
 */
@Produces(FenceFacet.class)
@Requires(@Facet(ParcelFacet.class))
public class FenceFacetProvider implements FacetProvider {

    private final LoadingCache<Parcel, Optional<SimpleFence>> cache = CacheBuilder.newBuilder().build(
            new CacheLoader<Parcel, Optional<SimpleFence>>() {

        @Override
        public Optional<SimpleFence> load(Parcel parcel) {
            return generateFence(parcel);
        }
    });


    private long seed;

    @Override
    public void setSeed(long seed) {
        this.seed = seed;
    }

    @Override
    public void process(GeneratingRegion region) {

        Border3D border = region.getBorderForFacet(FenceFacet.class);
        FenceFacet facet = new FenceFacet(region.getRegion(), border);

        ParcelFacet parcelFacet = region.getRegionFacet(ParcelFacet.class);

        for (Parcel parcel : parcelFacet.getParcels()) {
            Optional<SimpleFence> optFence = cache.getUnchecked(parcel);
            if (optFence.isPresent()) {
                facet.addFence(optFence.get());
            }
        }

        region.setRegionFacet(FenceFacet.class, facet);
    }

    private Optional<SimpleFence> generateFence(Parcel parcel) {

        Rect2i fenceRc = Rect2i.createFromMinAndMax(parcel.getShape().min(), parcel.getShape().max());
//        Rect2i fenceRc = new Rect2i(parcel.getShape());                // TODO: add copy constructor
        Orientation gateOrient = parcel.getOrientation();
        LineSegment seg = Edges.getEdge(fenceRc, parcel.getOrientation());
        Vector2i gatePos = new Vector2i(BaseVector2f.lerp(seg.getStart(), seg.getEnd(), 0.5f), RoundingMode.HALF_UP);

        return Optional.of(new SimpleFence(fenceRc, gateOrient, gatePos));
    }

}
