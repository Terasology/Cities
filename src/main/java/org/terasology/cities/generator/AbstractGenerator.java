/*
 * Copyright 2013 MovingBlocks
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.terasology.cities.generator;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

/**
 * Contains some general utility methods generators often use
 * @author Martin Steiger
 */
public class AbstractGenerator {

    /**
     * @param rc the rectangle to transform
     * @param bounds the bounding rectangle for the transformation (translation offset and rotation center)
     * @param rot the rotation in degrees (only multiples of 45deg.)
     * @return the translated and rotated rectangle
     */
    protected Rectangle transformRect(Rectangle rc, Rectangle bounds, int rot) {
        
        AffineTransform at = new AffineTransform();
        at.translate(bounds.x, bounds.y);
        at.rotate(Math.toRadians(rot), bounds.width * 0.5, bounds.height * 0.5);
        
        Point ptSrc1 = new Point(rc.x, rc.y);
        Point ptSrc2 = new Point(rc.x + rc.width, rc.y + rc.height);
        Point ptDst1 = new Point();
        Point ptDst2 = new Point();
        at.transform(ptSrc1, ptDst1);
        at.transform(ptSrc2, ptDst2);

        int x = Math.min(ptDst1.x, ptDst2.x);
        int y = Math.min(ptDst1.y, ptDst2.y);
        int width = Math.max(ptDst1.x, ptDst2.x) - x;
        int height = Math.max(ptDst1.y, ptDst2.y) - y;
        Rectangle result = new Rectangle(x, y, width, height);

        return result;
    }
}
