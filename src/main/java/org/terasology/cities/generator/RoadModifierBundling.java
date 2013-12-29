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

package org.terasology.cities.generator;

import java.util.Collection;
import java.util.Set;

import javax.vecmath.Point2i;
import javax.vecmath.Vector2d;

import org.terasology.cities.common.Point2iUtils;
import org.terasology.cities.model.Road;
import org.terasology.math.Vector2i;

import com.google.common.collect.Sets;

/**
 * Attraction forces act on road segments pulling close ones further together (until they overlap)
 * @author Martin Steiger
 */
public class RoadModifierBundling {

    /**
     * @param roads a set of all roads that act forces on each other
     * @return a new set of roads that contain a modified version of localRoads
     */
    public Set<Road> apply(Set<Road> roads) {

        Set<Road> newRoads = Sets.newHashSet();
        
        for (Road road : roads) {
            
            newRoads.add(attractRoads(road, roads));
        }

        return newRoads;
    }

    private Road attractRoads(Road road, Collection<Road> others) {

        Road newRoad = new Road(road.getStart(), road.getEnd());

        for (Point2i n : road.getPoints()) {
            Point2i np = new Point2i(n);

            for (Road other : others) {
                if (road.equals(other)) {
                    continue;
                }

                np.add(getInfluence(n, other.getPoints()));
            }

            newRoad.add(np);
        }

        return newRoad;
    }

    private Vector2i getInfluence(Point2i p, Collection<Point2i> points) {
        final double maxDist = 0.15;

        Vector2d influence = new Vector2d(0, 0);
        
        double fac = 0.02;
        
        for (Point2i op : points) {
            double distSq = Point2iUtils.distanceSquared(op, p);
            
            if (distSq < maxDist * maxDist) {
                double dist = Math.sqrt(distSq);
                double inf = (1.0 - dist / maxDist) * fac;
                Vector2d dir = new Vector2d(op.x, op.y);
                dir.x -= p.x;
                dir.y -= p.y;
                dir.scale(inf / dist);
                influence.add(dir);
            }
        }

        return new Vector2i((int) influence.x, (int) influence.y);
    }

}
