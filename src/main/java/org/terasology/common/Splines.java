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

package org.terasology.common;

import java.awt.geom.Path2D;
import java.util.List;

import javax.vecmath.Point2d;

/**
 * Some spline-related utilities
 * @author Martin Steiger
 */
public final class Splines {
    
    private Splines() {
        // private
    }
    
    /**
     * Create a bezier spline that passes through all given points
     * @param pts a list of points
     * @param smoothness the smoothness of the path
     * @return a path of connected curve segments
     */
    public static Path2D getBezierSplinePath(List<Point2d> pts, double smoothness) {
        Path2D path = new Path2D.Double();

        if (pts.isEmpty()) {
            return path;
        }
        
        path.moveTo(pts.get(0).x, pts.get(0).y);

        for (int i = 0; i < pts.size() - 1; i++) {
            Point2d p1 = pts.get(i + 1);
            Point2d c0 = computeNextControlPoint(pts, i, smoothness);
            Point2d c1 = computePreviousControlPoint(pts, i + 1, smoothness);
            path.curveTo(c0.x, c0.y, c1.x, c1.y, p1.x, p1.y);
        }

        return path;
    }
 
    private static Point2d add(Point2d a, Point2d b) {
        return new Point2d(a.x + b.x, a.y + b.y);
    }
    private static Point2d sub(Point2d a, Point2d b) {
        return new Point2d(a.x - b.x, a.y - b.y);
    }

    private static Point2d mul(Point2d p, double f) {
        return new Point2d(p.x * f, p.y * f);
    }

    private static Point2d computePreviousControlPoint(List<Point2d> points, int index, double smoothness) {
        if (index == points.size() - 1) {
            Point2d p0 = points.get(index - 1);
            Point2d p1 = points.get(index);
            Point2d p0to1 = sub(p1, p0);
            Point2d c = sub(p1, mul(p0to1, smoothness));
            return c;
        }
        Point2d p0 = points.get(index - 1);
        Point2d p1 = points.get(index);
        Point2d p2 = points.get(index + 1);
        Point2d p0to2 = sub(p2, p0);
        Point2d c = sub(p1, mul(p0to2, smoothness));
        return c;
    }

    private static Point2d computeNextControlPoint(List<Point2d> points, int index, double smoothness) {
        if (index == 0) {
            Point2d p0 = points.get(index);
            Point2d p1 = points.get(index + 1);
            Point2d p0to1 = sub(p1, p0);
            Point2d c = add(p0, mul(p0to1, smoothness));
            return c;
        }
        Point2d p0 = points.get(index - 1);
        Point2d p1 = points.get(index);
        Point2d p2 = points.get(index + 1);
        Point2d p0to2 = sub(p2, p0);
        Point2d c = add(p1, mul(p0to2, smoothness));
        return c;
    }
}
