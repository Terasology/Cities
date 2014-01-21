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
 * TODO Type description
 * @author Martin Steiger
 */
public class Plane2d {

    private double ax;
    private double ay;
    private double az;
    private double dx;
    private double dy;
    private double dz;

    public Plane2d(Point3d p0, Point3d p1) {
        ax = p0.x;
        ay = p0.y;
        az = p0.z;
        dx = p1.x - p0.x;
        dy = p1.y - p0.y;
        dz = p1.z - p0.z;
    }
    
    public double getLambda(double ex, double ey) {
        return (ey * dy - ay * dy - ax * dx + ex * dx) / (dy * dy + dx * dx);
    }

    public double getLambdaNorm(double ex, double ey) {
        return getLambda(ex, ey) * Math.sqrt(dx * dx + dy * dy);
    }    

    public double getZ(double ex, double ey) {
        return (int) (az + getLambda(ex, ey) * dz);
    }

    public double getKappa(double ex, double ey) {
        return (ax + getLambda(ex, ey) * dx - ex) / dy;
    }

    public double getKappaNorm(double ex, double ey) {
        return getKappa(ex, ey) * Math.sqrt(dx * dx + dy * dy);
    }    
}
