// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.cities.deco;

import org.terasology.cities.DefaultBlockType;
import org.terasology.commonworld.Orientation;
import org.terasology.engine.math.Side;
import org.terasology.math.geom.BaseVector3i;

import java.util.Collections;

/**
 * A straight, rising ladder made of a single block type
 */
public class Ladder extends ColumnDecoration {

    /**
     * @param basePos the position of the base block
     * @param o the orientation of the ladder (must be cardinal)
     * @param height the height of the ladder
     */
    public Ladder(BaseVector3i basePos, Orientation o, int height) {
        super(Collections.nCopies(height, DefaultBlockType.LADDER),
                Collections.nCopies(height, getSide(o)),
                basePos);
    }

    private static Side getSide(Orientation orientation) {
        switch (orientation) {
            case WEST:
                return Side.LEFT;
            case NORTH:
                return Side.FRONT;
            case EAST:
                return Side.RIGHT;
            case SOUTH:
                return Side.BACK;
            default:
                return null;
        }
    }

}
