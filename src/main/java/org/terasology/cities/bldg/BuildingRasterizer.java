/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.cities.bldg;

import org.terasology.cities.BlockTheme;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizer;
import org.terasology.world.generation.facets.SurfaceHeightFacet;

/**
 *
 */
public class BuildingRasterizer implements WorldRasterizer {

    private BlockTheme theme;

    /**
     * @param theme
     */
    public BuildingRasterizer(BlockTheme theme) {
        this.theme = theme;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        BuildingFacet buildingFacet = chunkRegion.getFacet(BuildingFacet.class);
        SurfaceHeightFacet heightFacet = chunkRegion.getFacet(SurfaceHeightFacet.class);
        for (Building bldg : buildingFacet.getBuildings()) {
            for (BuildingPart part : bldg.getParts()) {
                raster(chunk, part, heightFacet);
            }
        }
    }

    private void raster(CoreChunk chunk, BuildingPart part, SurfaceHeightFacet heightFacet) {
    }

}

