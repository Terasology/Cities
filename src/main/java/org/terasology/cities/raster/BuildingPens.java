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

package org.terasology.cities.raster;

import org.terasology.cities.BlockType;
import org.terasology.cities.DefaultBlockType;
import org.terasology.commonworld.heightmap.HeightMap;

/**
 * Provides {@link Pen} instances for building-related rasterization.
 */
public final class BuildingPens {

    private BuildingPens() {
        // no instances
    }

    /**
     * @param target the target to write to
     * @param terrainHeightMap the terrain height map
     * @param baseHeight the floor level
     * @param floor the floor block type
     * @return a new instance
     */
    public static Pen floorPen(RasterTarget target, HeightMap terrainHeightMap, int baseHeight, BlockType floor) {

        return new AbstractPen(target.getAffectedArea()) {

            @Override
            public void draw(int x, int z) {
                int terrain = terrainHeightMap.apply(x, z);
                int floorLevel = baseHeight - 1;
                int y = Math.max(target.getMinHeight(), terrain);
                if (y > target.getMaxHeight()) {
                    return;
                }

                // put foundation material below between terrain and floor level
                while (y < floorLevel) {
                    target.setBlock(x, y, z, DefaultBlockType.BUILDING_FOUNDATION);
                    y++;
                    if (y > target.getMaxHeight()) {
                        return;
                    }
                }

                // y can be larger than baseHeight here
                if (floorLevel < target.getMinHeight()) { // if the minY is fully above, we need to exit now
                    return;
                }

                // lay floor level
                target.setBlock(x, floorLevel, z, floor);
                y = floorLevel + 1;

                // clear area above floor level
                while (y <= target.getMaxHeight() && y <= terrain) {
                    target.setBlock(x, y, z, DefaultBlockType.AIR);
                    y++;
                }
            }
        };
    }
}
