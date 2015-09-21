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

package org.terasology.cities.door;

import org.terasology.commonworld.Orientation;
import org.terasology.math.geom.Rect2i;

/**
 * A simple, rectangular door with fixed height and orientation
 */
public class WingDoor implements Door {

    private final Orientation orientation;
    private final int baseHeight;
    private final int topHeight;
    private final Rect2i area;

    /**
     * @param orientation the orientation
     * @param area the door area (must be 2x1 or 1x2 sized)
     * @param baseHeight the height at the bottom
     * @param topHeight the height at the top
     */
    public WingDoor(Orientation orientation, Rect2i area, int baseHeight, int topHeight) {
        this.orientation = orientation;
        this.area = area;
        this.baseHeight = baseHeight;
        this.topHeight = topHeight;
    }

    /**
     * @return the orientation
     */
    public Orientation getOrientation() {
        return this.orientation;
    }

    /**
     * @return the door area
     */
    public Rect2i getArea() {
        return this.area;
    }

    /**
     * @return the baseHeight
     */
    public int getBaseHeight() {
        return this.baseHeight;
    }

    /**
     * @return the topHeight
     */
    public int getTopHeight() {
        return this.topHeight;
    }
}
