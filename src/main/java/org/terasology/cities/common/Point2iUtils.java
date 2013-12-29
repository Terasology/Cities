/*
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
