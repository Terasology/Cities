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

package org.terasology.cities.roof;

import org.terasology.cities.BlockTheme;
import org.terasology.cities.model.roof.Roof;
import org.terasology.cities.raster.ChunkRasterTarget;
import org.terasology.cities.raster.RasterTarget;
import org.terasology.cities.window.Window;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.math.TeraMath;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizer;
import org.terasology.world.generation.facets.SurfaceHeightFacet;

/**
 * @param <T> the target class
 */
public abstract class RoofRasterizer<T extends Roof> implements WorldRasterizer {

    private final BlockTheme theme;
    private final Class<T> targetClass;

    /**
     * @param theme the block theme that is used to map type to blocks
     * @param targetClass the target class that is rasterized
     */
    protected RoofRasterizer(BlockTheme theme, Class<T> targetClass) {
        this.theme = theme;
        this.targetClass = targetClass;
    }

    @Override
    public void initialize() {
        // nothing to do
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        SurfaceHeightFacet heightFacet = chunkRegion.getFacet(SurfaceHeightFacet.class);
        HeightMap hm = new HeightMap() {

            @Override
            public int apply(int x, int z) {
                return TeraMath.floorToInt(heightFacet.getWorld(x, z));
            }
        };
        RasterTarget brush = new ChunkRasterTarget(chunk, theme);
        RoofFacet buildingFacet = chunkRegion.getFacet(RoofFacet.class);
        for (Roof roof : buildingFacet.getRoofs()) {
            if (targetClass.isInstance(roof)) {
                raster(brush, targetClass.cast(roof), hm);
            }
        }
    }

    public void tryRaster(RasterTarget brush, Roof roof, HeightMap heightMap) {
        if (targetClass.isInstance(roof)) {
            raster(brush, targetClass.cast(roof), heightMap);
        }
    }

    protected abstract void raster(RasterTarget brush, T part, HeightMap heightMap);
}

