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

package org.terasology.cities.common;

import javax.vecmath.Point2i;

import org.terasology.math.TeraMath;

/**
 * Some {@link Point2i}-related utilities
 * @author Martin Steiger
 */
public final class Point2iUtils {
    
    private Point2iUtils() {
        // private constructor
    }
    
    /**
     * @param p0 one point
     * @param p1 the other point
     * @return the distance in between
     */
    public static double distance(Point2i p0, Point2i p1) {
        double dx;
        double dy; 

        dx = p0.x - p1.x;
        dy = p0.y - p1.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * @param p0 one point
     * @param p1 the other point
     * @return the squared distance
     */
    public static double distanceSquared(Point2i p0, Point2i p1) {
        double dx;
        double dy; 

        dx = p0.x - p1.x;
        dy = p0.y - p1.y;
        return dx * dx + dy * dy;
    }    
    
    /**
     * Linearly interpolates between p1 and p2 and returns the result
     * @param p1 the first point
     * @param p2 the second point
     * @param alpha the alpha interpolation parameter
     * @return the new point
     */
   public static Point2i interpolate(Point2i p1, Point2i p2, double alpha) { 
        int x = TeraMath.floorToInt(0.5 + (1 - alpha) * p1.x + alpha * p2.x);
        int y = TeraMath.floorToInt(0.5 + (1 - alpha) * p1.y + alpha * p2.y);

        return new Point2i(x, y);
   } 
    
}
