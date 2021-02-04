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

package org.terasology.cities.window;

import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.terasology.commonworld.Orientation;

/**
 * A single block window in a wall
 */
public class SimpleWindow implements Window {

    private final Orientation orientation;
    private final Vector2i pos = new Vector2i();
    private final int height;

    /**
     * @param orientation the orientation
     * @param pos the window position
     * @param height the height at the bottom
     */
    public SimpleWindow(Orientation orientation, Vector2ic pos, int height) {
        this.orientation = orientation;
        this.pos.set(pos);
        this.height = height;
    }

    /**
     * @return the orientation
     */
    public Orientation getOrientation() {
        return this.orientation;
    }

    /**
     * @return the window position
     */
    public Vector2ic getPos() {
        return this.pos;
    }

    /**
     * @return the baseHeight
     */
    public int getHeight() {
        return this.height;
    }
}
