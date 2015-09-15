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

package org.terasology.cities;

import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import org.terasology.math.geom.Circle;
import org.terasology.math.geom.Rect2f;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Shape;

/**
 *
 */
public final class AwtConverter {

    private AwtConverter() {
        // no instances
    }

    /**
     * Converts shapes to AWT shapes
     * @param shape the shape to convert
     * @return the AWT shape instance
     * @throws IllegalArgumentException if no mapping exists
     */
    public static java.awt.Shape toAwt(Shape shape) {
        if (shape instanceof Rect2i) {
            return toAwt((Rect2i) shape);
        }

        if (shape instanceof Rect2f) {
            return toAwt((Rect2f) shape);
        }

        if (shape instanceof Circle) {
            return toAwt((Circle) shape);
        }

        throw new IllegalArgumentException("Not recognized: " + shape);
    }

    public static java.awt.Rectangle toAwt(Rect2i rc) {
        return new Rectangle(rc.minX(), rc.minY(), rc.width(), rc.height());
    }

    public static java.awt.geom.Rectangle2D toAwt(Rect2f rc) {
        return new Rectangle2D.Float(rc.minX(), rc.minY(), rc.width(), rc.height());
    }

    public static java.awt.geom.Ellipse2D toAwt(Circle circle) {
        float minX = circle.getCenter().getX() - circle.getRadius();
        float minY = circle.getCenter().getY() - circle.getRadius();
        float dia = circle.getRadius() * 2f;
        return new Ellipse2D.Float(minX, minY, dia, dia);
    }

}
