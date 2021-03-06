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

import org.joml.Vector3ic;
import org.terasology.cities.BlockType;
import org.terasology.engine.math.Side;
import org.terasology.engine.world.block.BlockAreac;
import org.terasology.engine.world.block.BlockRegionc;

import java.util.Set;

/**
 * Converts model elements into blocks
 */
public interface RasterTarget {

    /**
     * @param x x in world coords
     * @param y y in world coords
     * @param z z in world coords
     * @param type the block type
     */
    void setBlock(int x, int y, int z, BlockType type);

    /**
     * @param pos the position in world coords
     * @param type the block type
     */
    default void setBlock(Vector3ic pos, BlockType type) {
        setBlock(pos.x(), pos.y(), pos.z(), type);
    }

    /**
     * @param x x in world coords
     * @param y y in world coords
     * @param z z in world coords
     * @param type the block type
     * @param side the sides (used to find the correct block from the family)
     */
    void setBlock(int x, int y, int z, BlockType type, Set<Side> side);

    /**
     * @param pos the position in world coords
     * @param type the block type
     * @param sides the sides (used to find the correct block from the family)
     */
    default void setBlock(Vector3ic pos, BlockType type, Set<Side> sides) {
        setBlock(pos.x(), pos.y(), pos.z(), type, sides);
    }

    /**
     * @return the maximum drawing height
     */
    default int getMaxHeight() {
        return getAffectedRegion().maxY();
    }

    /**
     * @return the maximum drawing height
     */
    default int getMinHeight() {
        return getAffectedRegion().minY();
    }

    /**
     * @return the XZ area that is drawn by this raster target
     */
    BlockAreac getAffectedArea();

    /**
     * @return the region that is drawn by this raster target
     */
    BlockRegionc getAffectedRegion();
}
