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
import org.terasology.cities.raster.AbstractPen;
import org.terasology.cities.raster.ChunkRasterTarget;
import org.terasology.cities.raster.Pen;
import org.terasology.cities.raster.RasterTarget;
import org.terasology.cities.raster.RasterUtil;
import org.terasology.cities.surface.InfiniteSurfaceHeightFacet;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Rect2i;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizer;

/**
 * @param <T> the target class
 */
public abstract class BuildingPartRasterizer<T> implements WorldRasterizer {

    private final BlockTheme theme;
    private final Class<T> targetClass;

    /**
     * @param theme the block theme that is used to map type to blocks
     * @param targetClass the target class that is rasterized
     */
    protected BuildingPartRasterizer(BlockTheme theme, Class<T> targetClass) {
        this.theme = theme;
        this.targetClass = targetClass;
    }

    @Override
    public void initialize() {
        // nothing to do
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        RasterTarget brush = new ChunkRasterTarget(chunk, theme);
        raster(brush, chunkRegion);
    }

    public void raster(RasterTarget brush, Region chunkRegion) {
        BuildingFacet buildingFacet = chunkRegion.getFacet(BuildingFacet.class);
        InfiniteSurfaceHeightFacet heightFacet = chunkRegion.getFacet(InfiniteSurfaceHeightFacet.class);
        HeightMap hm = new HeightMap() {

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

    protected abstract void raster(RasterTarget brush, T part, HeightMap heightMap);

    /**
     * @param target the target to write to
     * @param rc the rectangle to prepare
     * @param terrain the terrain height map
     * @param baseHeight the floor level
     * @param floor the floor block type
     */
    protected static void prepareFloor(RasterTarget target, Rect2i rc, HeightMap terrainHeightMap, int baseHeight, BlockTypes floor) {

        Pen floorPen = new AbstractPen(target.getAffectedArea()) {

            @Override
            public void draw(int x, int z) {
                int terrain = terrainHeightMap.apply(x, z);
                int y = Math.max(target.getMinHeight(), terrain);
                if (y > target.getMaxHeight()) {
                    return;
                }

                // put foundation material below between terrain and floor level
                while (y < baseHeight) {
                    target.setBlock(x, y, z, BlockTypes.BUILDING_FOUNDATION);
                    y++;
                    if (y > target.getMaxHeight()) {
                        return;
                    }
                }

                // y can be larger than baseHeight here
                if (baseHeight < target.getMinHeight()) { // if the minY is fully above, we need to exit now
                    return;
                }

                // lay floor level
                target.setBlock(x, baseHeight, z, floor);
                y = baseHeight + 1;

                // clear area above floor level
                while (y <= target.getMaxHeight() && y <= terrain) {
                    target.setBlock(x, y, z, BlockTypes.AIR);
                    y++;
                }
            }
        };

        RasterUtil.fillRect(floorPen, rc);
    }
}

