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
import org.terasology.cities.BlockTypes;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.ChunkBrush;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.commonworld.heightmap.HeightMapAdapter;
import org.terasology.commonworld.heightmap.HeightMaps;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Rect2i;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizer;
import org.terasology.world.generation.facets.SurfaceHeightFacet;

/**
 * @param <T> the target class
 */
public abstract class BuildingPartRasterizer<T> implements WorldRasterizer {

    private final BlockTheme theme;
    private final Class<T> targetClass;

    /**
     * @param theme
     * @param targetClass
     */
    public BuildingPartRasterizer(BlockTheme theme, Class<T> targetClass) {
        this.theme = theme;
        this.targetClass = targetClass;
    }

    @Override
    public void initialize() {
        // nothing to do
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        Brush brush = new ChunkBrush(chunk, theme);
        raster(brush, chunkRegion);
    }

    public void raster(Brush brush, Region chunkRegion) {
        BuildingFacet buildingFacet = chunkRegion.getFacet(BuildingFacet.class);
        SurfaceHeightFacet heightFacet = chunkRegion.getFacet(SurfaceHeightFacet.class);
        HeightMap hm = new HeightMapAdapter() {

            @Override
            public int apply(int x, int z) {
                return TeraMath.floorToInt(heightFacet.getWorld(x, z));
            }
        };
        for (Building bldg : buildingFacet.getBuildings()) {
            for (BuildingPart part : bldg.getParts()) {
                if (targetClass.isInstance(part)) {
                    raster(brush, targetClass.cast(part), hm);
                }
            }
        }
    }

    protected abstract void raster(Brush brush, T part, HeightMap heightMap);

    /**
     * @param brush the brush to use
     * @param rc the rectangle to prepare
     * @param terrain the terrain height map
     * @param baseHeight the floor level
     * @param floor the floor block type
     */
    protected void prepareFloor(Brush brush, Rect2i rc, HeightMap terrain, int baseHeight, BlockTypes floor) {

        // clear area above floor level
        brush.fillRect(rc, baseHeight, HeightMaps.offset(terrain, 1), BlockTypes.AIR);

        // lay floor level
        brush.fillRect(rc, baseHeight - 1, baseHeight, floor);

        // put foundation concrete below
        brush.fillRect(rc, terrain, baseHeight - 1, BlockTypes.BUILDING_FOUNDATION);
    }
}

