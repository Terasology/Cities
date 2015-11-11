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

package org.terasology.cities.raster;

import org.terasology.cities.BlockType;
import org.terasology.commonworld.heightmap.HeightMap;

/**
 * A collections of {@Pen} factory methods.
 */
public final class Pens {

    private Pens() {
        // no instances
    }

    /**
     * @param target the target object
     * @param bottomHeight the bottom height (inclusive)
     * @param topHeight the top height (exclusive)
     * @param type the block type
     * @return a new instance
     */
    public static Pen fill(RasterTarget target, int bottomHeight, int topHeight, BlockType type) {
        int bot = Math.max(target.getMinHeight(), bottomHeight);
        int top = Math.min(target.getMaxHeight(), topHeight - 1);  // top layer is exclusive
        return new AbstractPen(target.getAffectedArea()) {

            @Override
            public void draw(int x, int z) {
                for (int y = bot; y <= top; y++) {
                    target.setBlock(x, y, z, type);
                }
            }
        };
    }

    /**
     * @param target the target object
     * @param hmBottom the bottom height map (inclusive)
     * @param hmTop the top height map (exclusive)
     * @param type the block type
     * @return a new instance
     */
    public static Pen fill(RasterTarget target, HeightMap hmBottom, HeightMap hmTop, BlockType type) {
        return new AbstractPen(target.getAffectedArea()) {

            @Override
            public void draw(int x, int z) {
                int bot = Math.max(target.getMinHeight(), hmBottom.apply(x, z));
                int top = Math.min(target.getMaxHeight(), hmTop.apply(x, z) - 1);  // top layer is exclusive
                for (int y = bot; y <= top; y++) {
                    target.setBlock(x, y, z, type);
                }
            }
        };
    }

    /**
     * @param target the target object
     * @param hm the height map
     * @param type the block type
     * @return a new instance
     */
    public static Pen singleLayer(RasterTarget target, HeightMap hm, BlockType type) {
        return new AbstractPen(target.getAffectedArea()) {

            @Override
            public void draw(int x, int z) {
                int y = hm.apply(x, z);
                if (y >= target.getMinHeight() && y <= target.getMaxHeight()) {
                    target.setBlock(x, y, z, type);
                }
            }
        };
    }
}
