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
import java.util.Collections;
import java.util.List;

public class Contour {

    private final List<Point> points = new ArrayList<Point>();

    public void addPoint(Point n) {
        points.add(n);
    }

    public Polygon makePolygon() {
        int m = points.size();
        
        int[] xPoints = new int[m];
        int[] yPoints = new int[m];
        int idx = 0;
        for (Point cpt : points) {
            xPoints[idx] = cpt.x;
            yPoints[idx] = cpt.y;
            idx++;
        }
        return new Polygon(xPoints, yPoints, m);
    }

    public void moveBy(int dx, int dy) {
        for (Point pt : points) {
            pt.translate(dx, dy);
        }
    }

    /**
     * @return an unmodifiable view on the points
     */
    public List<Point> getPoints() {
        return Collections.unmodifiableList(points);
    }

}
