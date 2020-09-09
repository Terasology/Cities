// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.cities.raster;

import org.terasology.cities.BlockType;
import org.terasology.engine.math.Region3i;
import org.terasology.engine.math.Side;
import org.terasology.math.geom.BaseVector3i;
import org.terasology.math.geom.Rect2i;

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
    default void setBlock(BaseVector3i pos, BlockType type) {
        setBlock(pos.getX(), pos.getY(), pos.getZ(), type);
    }

    /**
     * @param x x in world coords
     * @param y y in world coords
     * @param z z in world coords
     * @param type the block type
     * @param sides the sides (used to find the correct block from the family)
     */
    void setBlock(int x, int y, int z, BlockType type, Set<Side> side);

    /**
     * @param pos the position in world coords
     * @param type the block type
     * @param sides the sides (used to find the correct block from the family)
     */
    default void setBlock(BaseVector3i pos, BlockType type, Set<Side> sides) {
        setBlock(pos.getX(), pos.getY(), pos.getZ(), type, sides);
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
    Rect2i getAffectedArea();

    /**
     * @return the region that is drawn by this raster target
     */
    Region3i getAffectedRegion();
}
