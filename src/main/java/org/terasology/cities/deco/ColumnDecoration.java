// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.cities.deco;

import com.google.common.base.Preconditions;
import org.terasology.cities.BlockType;
import org.terasology.engine.math.Side;
import org.terasology.math.geom.BaseVector3i;
import org.terasology.math.geom.ImmutableVector3i;

import java.util.List;

/**
 * A decoration made up by a column of blocks (e.g. ladders, pillars, etc.)
 */
public class ColumnDecoration implements Decoration {

    private final ImmutableVector3i pos;
    private final List<BlockType> blocks;
    private final List<Side> sides;

    /**
     * @param blocks the decoration block types
     * @param sides the facing sides of the blocks
     * @param basePos the window position
     */
    public ColumnDecoration(List<BlockType> blocks, List<Side> sides, BaseVector3i basePos) {
        Preconditions.checkArgument(blocks.size() == sides.size(), "blockCount != sideCount");
        this.blocks = blocks;
        this.sides = sides;
        this.pos = ImmutableVector3i.createOrUse(basePos);
    }

    /**
     * @return the position
     */
    public ImmutableVector3i getBasePos() {
        return this.pos;
    }

    /**
     * @return the block type to raster
     */
    public List<BlockType> getBlockTypes() {
        return blocks;
    }

    /**
     * @return the facing sides of the blocks
     */
    public List<Side> getSides() {
        return sides;
    }

    /**
     * @return the height of the column
     */
    public int getHeight() {
        return blocks.size(); // the lists are equally long
    }
}
