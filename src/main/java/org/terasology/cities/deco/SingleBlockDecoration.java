// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.cities.deco;

import org.terasology.cities.BlockType;
import org.terasology.engine.math.Side;
import org.terasology.math.geom.BaseVector3i;
import org.terasology.math.geom.ImmutableVector3i;

/**
 * A single block decoration
 */
public class SingleBlockDecoration implements Decoration {

    private final ImmutableVector3i pos;
    private final BlockType type;
    private final Side side;

    /**
     * @param type the decoration type
     * @param pos the window position
     * @param side the direction of the decoration
     */
    public SingleBlockDecoration(BlockType type, BaseVector3i pos, Side side) {
        this.type = type;
        this.side = side;
        this.pos = ImmutableVector3i.createOrUse(pos);
    }

    /**
     * @return the position
     */
    public ImmutableVector3i getPos() {
        return this.pos;
    }

    /**
     * @return the block type to raster
     */
    public BlockType getType() {
        return type;
    }

    /**
     * @return the orientation of the block
     */
    public Side getSide() {
        return side;
    }
}
