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

package org.terasology.cities.raster;

import org.terasology.math.geom.Rect2i;

/**
 * A {@link Pen} that checks all write operations before calling the delegate.
 */
public class CheckedPen implements Pen {

    private final Pen pen;

    /**
     * @param pen the underlying instance to constrain
     */
    public CheckedPen(Pen pen) {
        this.pen = pen;
    }

    @Override
    public void draw(int x, int z) {
        if (pen.getTargetArea().contains(x, z)) {
            pen.draw(x, z);
        }
    }

    @Override
    public Rect2i getTargetArea() {
        return pen.getTargetArea();
    }
}
