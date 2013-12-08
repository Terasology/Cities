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

package org.terasology.world.generator.city.def;

import java.util.Collection;
import java.util.Set;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.terasology.world.generator.city.model.Road;

import com.google.common.collect.Sets;

/**
 * Attraction forces act on road segments pulling close ones further together (until they overlap)
 * @author Martin Steiger
 */
public class RoadModifierBundling {

    /**
     * @param localRoads a set of all roads that are adjusted
     * @param allRoads a set of all roads that act forces on localRoads
     * @return a new set of roads that contain a modified version of localRoads
     */
    public Set<Road> apply(Set<Road> localRoads, Set<Road> allRoads) {

        Set<Road> newRoads = Sets.newHashSet();
        
        for (Road road : localRoads) {
            
            Road road2 = snapRoad(road, allRoads);
            newRoads.add(road2);
        }
        
        return newRoads;
    }

    private Road snapRoad(Road road, Collection<Road> others) {

        Road newRoad = new Road(road.getStart(), road.getEnd());

        for (Point2d n : road.getPoints()) {
            Point2d np = new Point2d(n);

            for (Road other : others) {
                if (road.equals(other)) {
                    continue;
                }

                applyInfluence(np, other.getPoints());
            }

            newRoad.add(np);
        }

        return newRoad;
    }

    private void applyInfluence(Point2d p, Collection<Point2d> points) {
        final double maxDist = 0.2;

        Point2d influence = new Point2d(0, 0);
        
        for (Point2d op : points) {
            double distSq = op.distanceSquared(p);
            
            if (distSq < maxDist * maxDist) {
                double dist = Math.sqrt(distSq);
                double inf = 1.0 - dist / maxDist;
                Vector2d dir = new Vector2d(p);
                dir.sub(op);
                dir.scale(inf / dist);
                influence.add(dir);
            }
        }

        influence.scale(0.5);   // move to the center at maximum

        p.add(influence);
    }

}
