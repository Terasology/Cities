/*
 * Copyright 2014 MovingBlocks
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
package org.terasology.cities.surface;

import org.terasology.math.geom.BaseVector2i;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.Requires;
import org.terasology.world.generation.facets.SurfaceHeightFacet;

/**
 * Uses the infinite surface to store the "core" region of the facet in a 2D array.
 */
@Produces(SurfaceHeightFacet.class)
@Requires(@Facet(InfiniteSurfaceHeightFacet.class))
public class SurfaceHeightFacetProvider implements FacetProvider {

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(SurfaceHeightFacet.class);
        SurfaceHeightFacet facet = new SurfaceHeightFacet(region.getRegion(), border);
        InfiniteSurfaceHeightFacet infiniteFacet = region.getRegionFacet(InfiniteSurfaceHeightFacet.class);

        for (BaseVector2i pos : facet.getWorldRegion().contents()) {
            int x = pos.getX();
            int y = pos.getY();
            facet.setWorld(pos, infiniteFacet.getWorld(x, y));
        }

        region.setRegionFacet(SurfaceHeightFacet.class, facet);
    }
}
