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
 * A roof with rectangular shape
 */
public class RectangularRoof extends AbstractRoof {

    private Rect2i baseRect;

    /**
     * @param baseRect the building rectangle (must be fully inside <code>withEaves</code>).
     * @param withEaves the roof area including eaves (=overhang)
     * @param baseHeight the base height of the roof
     */
    public RectangularRoof(Rect2i baseRect, Rect2i withEaves, int baseHeight) {
        super(withEaves, baseHeight);
        this.baseRect = baseRect;
    }

    /**
     * @return the roof area including eaves
     */
    public Rect2i getBaseArea() {
        return baseRect;
    }

    /**
     * @return the roof area including eaves
     */
    @Override
    public Rect2i getArea() {
        return (Rect2i) super.getArea();
    }
}
