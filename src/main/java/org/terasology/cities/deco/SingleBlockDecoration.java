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

import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.cities.BlockType;
import org.terasology.engine.math.Side;

/**
 * A single block decoration
 */
public class SingleBlockDecoration implements Decoration {

    private final Vector3i pos = new Vector3i();
    private BlockType type;
    private Side side;

    /**
     * @param type the decoration type
     * @param pos the window position
     * @param side the direction of the decoration
     */
    public SingleBlockDecoration(BlockType type, Vector3ic pos, Side side) {
        this.type = type;
        this.side = side;
        this.pos.set(pos);
    }

    /**
     * @return the position
     */
    public Vector3ic getPos() {
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
