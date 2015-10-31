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
 * A flat roof with an extruded border (terrace roof)
 */
public class FlatRoof extends RectangularRoof {

    private final int borderHeight;

    /**
     * @param baseRect the building rectangle (must be fully inside <code>withEaves</code>).
     * @param withEaves the roof area including eaves (=overhang)
     * @param baseHeight the base height of the roof
     * @param borderHeight the height of the border
     */
    public FlatRoof(Rect2i baseRect, Rect2i withEaves, int baseHeight, int borderHeight) {
        super(baseRect, withEaves, baseHeight);

        this.borderHeight = borderHeight;
    }

    /**
     * @param lx x in local (roof area) coordinates
     * @param lz z in local (roof area) coordinates
     * @return the borderHeight
     */
    public int getBorderHeight(int lx, int lz) {
        return borderHeight;
    }
}
