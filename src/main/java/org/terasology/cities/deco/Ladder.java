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

package org.terasology.cities.deco;

import org.joml.Vector3ic;
import org.terasology.cities.DefaultBlockType;
import org.terasology.commonworld.Orientation;
import org.terasology.math.Side;

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
    public Ladder(Vector3ic basePos, Orientation o, int height) {
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
