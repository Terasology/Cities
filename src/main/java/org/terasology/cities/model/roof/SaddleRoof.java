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

package org.terasology.cities.model.roof;

import com.google.common.base.Preconditions;
import org.terasology.commonworld.Orientation;
import org.terasology.engine.world.block.BlockAreac;

import static org.terasology.commonworld.Orientation.EAST;
import static org.terasology.commonworld.Orientation.NORTH;
import static org.terasology.commonworld.Orientation.SOUTH;
import static org.terasology.commonworld.Orientation.WEST;

/**
 * A saddle(gable) roof
 */
public class SaddleRoof extends RectangularRoof {

    private final double pitch;

    private final Orientation orientation;

    /**
     * @param baseRect the building rectangle (must be fully inside <code>withEaves</code>).
     * @param withEaves the roof area including eaves (=overhang)
     * @param baseHeight the base height of the roof
     * @param pitch the roof pitch
     * @param orientation the orientation (only NORTH, WEST, SOUTH, EAST are allowed)
     */
    public SaddleRoof(BlockAreac baseRect, BlockAreac withEaves, int baseHeight, Orientation orientation, double pitch) {
        super(baseRect, withEaves, baseHeight);

        Preconditions.checkArgument(pitch > 0 && pitch < 10, "pitch must be in [0..10]");

        Preconditions.checkArgument(
                orientation == WEST
             || orientation == NORTH
             || orientation == SOUTH
             || orientation == EAST,
                "only NORTH, WEST, SOUTH, EAST are allowed");

        this.orientation = orientation;
        this.pitch = pitch;
    }

    /**
     * @return the pitch
     */
    public double getPitch() {
        return pitch;
    }

    /**
     * @return the orientation
     */
    public Orientation getOrientation() {
        return orientation;
    }

}
