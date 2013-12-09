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

import javax.vecmath.Point2d;

import org.terasology.common.UnorderedPair;
import org.terasology.world.generator.city.model.City;
import org.terasology.world.generator.city.model.Junction;
import org.terasology.world.generator.city.model.Road;

import com.google.common.base.Function;

/**
 * Creates a simple, straight, road with no segments for a connection 
 * @author Martin Steiger
 */
public class RoadGeneratorSimple implements Function<UnorderedPair<City>, Road> {
    
    private Function<Point2d, Junction> junctions;
    private double avgSegmentLength = 0.1;

    /**
     * @param junctions gives junctions based on location
     */
    public RoadGeneratorSimple(Function<Point2d, Junction> junctions) {
        this.junctions = junctions;
    }
    
    @Override
    public Road apply(UnorderedPair<City> pair) {

        City a = pair.getA();
        City b = pair.getB();
        
        Point2d posA = a.getPos();
        Point2d posB = b.getPos();

        Junction junA = junctions.apply(posA);
        Junction junB = junctions.apply(posB);
        Road road = new Road(junA, junB);

        addSegments(road, avgSegmentLength);

        // here we define width as the log of the smaller city's size
        double avgSize = Math.min(a.getSize(), b.getSize());
        float width = (float) Math.max(1.0, Math.log(avgSize));
        width = (float) Math.floor(width * 0.5);
        road.setWidth(1.0f);

        return road;
    }

    /**
     * @param road the road
     * @param avgDist average length of a segment measured in sectors
     * @return a road with segments
     */
    protected Road addSegments(Road road, double avgDist) {
        Point2d coordsA = road.getStart().getCoords();
        Point2d coordsB = road.getEnd().getCoords();
        
        double dist = coordsA.distance(coordsB);

        int segments = (int) (dist / avgDist + 0.5);

        for (int i = 1; i < segments; i++) {
            Point2d p = new Point2d(coordsA);
            p.interpolate(coordsB, i / (double) segments);

            road.add(p);
        }

        return road;
    }
}
