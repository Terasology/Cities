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

package org.terasology.cities.contour;

import java.awt.Point;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * Removes all points that lie on straight lines
 * @author Martin Steiger
 */
public final class ContourSimplifier {

    private ContourSimplifier() {
        // empty
    }
    
    /**
     * Removes all points that lie on straight lines
     * @param pts the list of points
     * @return a new list containing the points
     */
    public static List<Point> simplify(Collection<Point> pts) {

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

}

