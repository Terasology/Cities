/*
 * Copyright 2013 MovingBlocks
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

package org.terasology.cities.contour;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * Stores information on a contour
 * @author Martin Steiger
 */
public class Contour {

    /**
     * It would be nice a have a list implementation where contains() runs in O(1). 
     * Unfortunately, LinkedHashSet removes duplicate entries. 
     */
    private final Collection<Point> points = new ArrayList<Point>();
    private Collection<Point> simplifiedPoints;
    private Polygon polygon;

    /**
     * @param n the point to add
     */
    public void addPoint(Point n) {
        points.add(new Point(n));
        
        simplifiedPoints = null;
        polygon = null;
    }

    /**
     * @param pts the point collection to use
     * @return an AWT polygon of the data
     */
    private static Polygon createPolygon(Collection<Point> pts) {
        int m = pts.size();
        
        int[] xPoints = new int[m];
        int[] yPoints = new int[m];
        int idx = 0;
        for (Point cpt : pts) {
            xPoints[idx] = cpt.x;
            yPoints[idx] = cpt.y;
            idx++;
        }
        return new Polygon(xPoints, yPoints, m);
    }

    
    /**
     * Removes all points that lie on straight lines
     * @param pts the list of points
     * @return a <b>new collection</b> containing the points
     */
    private static List<Point> simplify(Collection<Point> pts) {

        if (pts.size() < 2) {
            return Lists.newArrayList(pts);
        }

        List<Point> result = Lists.newArrayList();

        Point prev = pts.iterator().next();
        Point dir = new Point();

        for (Point p : pts) {
            Point newdir = new Point(p.x - prev.x, p.y - prev.y);
            if (!newdir.equals(dir)) {
                result.add(prev);
                dir = newdir;
            }
            prev = p;

        }

        result.add(prev);

        return result;
    }
    
    /**
     * @return an unmodifiable sorted view on the points
     */
    public Collection<Point> getPoints() {
        return Collections.unmodifiableCollection(points);
    }

    /**
     * @return a simplified version of the curve, containing only points at direction changes 
     */
    public Collection<Point> getSimplifiedCurve() {
        if (simplifiedPoints == null) {
            simplifiedPoints = simplify(points);
        }

        return simplifiedPoints;
    }
    
    /**
     * @return a polygon representing the curve
     */
    public Polygon getPolygon() {
        if (polygon == null) {
            polygon = createPolygon(getSimplifiedCurve());
        }
        
        return polygon;
    }
    
    /**
     * A point is considered to lie inside if and only if:
     * <ul>
     * <li> it lies completely inside the boundary <i>or</i>
     * <li>
     * it lies exactly on the boundary and the
     * space immediately adjacent to the
     * point in the increasing <code>X</code> direction is
     * entirely inside the boundary <i>or</i>
     * <li>
     * it lies exactly on a horizontal boundary segment and the
     * space immediately adjacent to the point in the
     * increasing <code>Y</code> direction is inside the boundary.
     * </ul>
     * @param x the x coord
     * @param y the y coord
     * @return true if inside
     */
    public boolean isInside(double x, double y) {
        return getPolygon().contains(x, y);
    }
}
