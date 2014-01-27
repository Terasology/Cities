/*
 * Copyright 2014 MovingBlocks
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

import javax.vecmath.Point3d;

/**
 * A plane that is defined by two points.
 * The missing third point is derived by a horizontal (dz = 0) line.
 * The result is a ramp.
 * <pre>
 *  ( ex )   ( ax )     ( dx )     ( -dy )
 *  ( ey ) = ( ay ) + L ( dy ) + K (  dx )
 *  ( ez )   ( az )     ( dz )     (  0  )
 *
 *  ex = ax + L*dx - K*dy
 *
 *        ax + L*dx - ex
 *  K = ------------------
 *             dy
 *
 *
 *  ey = ay + L*dy + K*dx
 *
 *        (ey*dy - ay*dy - ax*dx + ex*dx)
 *  L = -----------------------------------
 *                  (dy^2 + dx^2)
 *
 *  ez = az * L*dz
 * </pre>
 * 
 * @author Martin Steiger
 */
public class Plane2d {

    private double ax;
    private double ay;
    private double az;
    private double dx;
    private double dy;
    private double dz;

    
    /**
     * @param p0 the first point
     * @param p1 the second point
     */
    public Plane2d(Point3d p0, Point3d p1) {
        ax = p0.x;
        ay = p0.y;
        az = p0.z;
        dx = p1.x - p0.x;
        dy = p1.y - p0.y;
        dz = p1.z - p0.z;
    }
    
    /**
     * Finds the lambda value <b>along<b> the line. p0 has a lambda of 0, p1 has a lambda of 1
     * @param ex point x
     * @param ey point y
     * @return the lambda value
     */
    public double getLambda(double ex, double ey) {
        return (ey * dy - ay * dy - ax * dx + ex * dx) / (dy * dy + dx * dx);
    }

    /**
     * Finds the norm. lambda value <b>along<b> the line. p0 has a norm. lambda of 0, p1 has a lambda of dist(p0, p1).
     * @param ex point x
     * @param ey point y
     * @return the normalized lambda value
     */
    public double getLambdaNorm(double ex, double ey) {
        return getLambda(ex, ey) * Math.sqrt(dx * dx + dy * dy);
    }    

    /**
     * @param ex point x
     * @param ey point y
     * @return the z value at that point (depends on lambda only)
     */
    public double getZ(double ex, double ey) {
        return (int) (az + getLambda(ex, ey) * dz + 0.5);
    }

    /**
     * Finds the distance from the line p0, p1 relative to that distance
     * @param ex point x
     * @param ey point y
     * @return the kappa value
     */
    public double getKappa(double ex, double ey) {
        return (ax + getLambda(ex, ey) * dx - ex) / dy;
    }

    /**
     * Finds the distance from the line p0, p1 in absolute units
     * @param ex point x
     * @param ey point y
     * @return the norm. kappa value
     */
    public double getKappaNorm(double ex, double ey) {
        return getKappa(ex, ey) * Math.sqrt(dx * dx + dy * dy);
    }    
}
