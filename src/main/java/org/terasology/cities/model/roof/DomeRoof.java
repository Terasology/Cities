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

import org.terasology.math.geom.Rect2i;

/**
 * A dome roof
 */
public class DomeRoof extends RectangularRoof {

    private final int height;

    /**
     * @param baseRect the building rectangle (must be fully inside <code>withEaves</code>).
     * @param withEaves the roof area including eaves (=overhang)
     * @param baseHeight the base height of the roof
     * @param maxHeight the maximum height of the roof
     */
    public DomeRoof(Rect2i baseRect, Rect2i withEaves, int baseHeight, int maxHeight) {
        super(baseRect, withEaves, baseHeight);

        this.height = maxHeight;
    }

    /**
     * @return the height of the dome
     */
    public int getHeight() {
        return this.height;
    }
}
