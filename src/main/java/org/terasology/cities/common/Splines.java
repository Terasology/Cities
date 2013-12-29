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

import java.awt.geom.Path2D;
import java.util.List;

import javax.vecmath.Point2i;

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
    public static Path2D getBezierSplinePath(List<Point2i> pts, double smoothness) {
        Path2D path = new Path2D.Double();

        if (pts.isEmpty()) {
            return path;
        }
        
        path.moveTo(pts.get(0).x, pts.get(0).y);

        for (int i = 0; i < pts.size() - 1; i++) {
            Point2i p1 = pts.get(i + 1);
            Point2i c0 = computeNextControlPoint(pts, i, smoothness);
            Point2i c1 = computePreviousControlPoint(pts, i + 1, smoothness);
            path.curveTo(c0.x, c0.y, c1.x, c1.y, p1.x, p1.y);
        }

        return path;
    }
 
    private static Point2i add(Point2i a, Point2i b) {
        return new Point2i(a.x + b.x, a.y + b.y);
    }
    private static Point2i sub(Point2i a, Point2i b) {
        return new Point2i(a.x - b.x, a.y - b.y);
    }

    private static Point2i mul(Point2i p, double f) {
        return new Point2i((int) (p.x * f), (int) (p.y * f));
    }

    private static Point2i computePreviousControlPoint(List<Point2i> points, int index, double smoothness) {
        if (index == points.size() - 1) {
            Point2i p0 = points.get(index - 1);
            Point2i p1 = points.get(index);
            Point2i p0to1 = sub(p1, p0);
            Point2i c = sub(p1, mul(p0to1, smoothness));
            return c;
        }
        Point2i p0 = points.get(index - 1);
        Point2i p1 = points.get(index);
        Point2i p2 = points.get(index + 1);
        Point2i p0to2 = sub(p2, p0);
        Point2i c = sub(p1, mul(p0to2, smoothness));
        return c;
    }

    private static Point2i computeNextControlPoint(List<Point2i> points, int index, double smoothness) {
        if (index == 0) {
            Point2i p0 = points.get(index);
            Point2i p1 = points.get(index + 1);
            Point2i p0to1 = sub(p1, p0);
            Point2i c = add(p0, mul(p0to1, smoothness));
            return c;
        }
        Point2i p0 = points.get(index - 1);
        Point2i p1 = points.get(index);
        Point2i p2 = points.get(index + 1);
        Point2i p0to2 = sub(p2, p0);
        Point2i c = add(p1, mul(p0to2, smoothness));
        return c;
    }
}
