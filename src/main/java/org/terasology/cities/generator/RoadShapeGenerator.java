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

import java.awt.BasicStroke;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.Set;

import javax.vecmath.Point2i;

import org.terasology.cities.common.Splines;
import org.terasology.cities.model.Junction;
import org.terasology.cities.model.Road;
import org.terasology.cities.model.Sector;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Renders roads using splines that pass though all segment points
 * @author Martin Steiger
 */
public class RoadShapeGenerator implements Function<Sector, Shape> {

    private final Function<Sector, Set<Road>> roadFunc;
    
    /**
     * @param roadFunc the road function
     */
    public RoadShapeGenerator(Function<Sector, Set<Road>> roadFunc) {
        this.roadFunc = roadFunc;
    }

    /**
     * @param sector the rendered sector
     * @return the area that contains all roads 
     */
    @Override
    public Shape apply(Sector sector) {

        Path2D allPaths = new Path2D.Double();
        
        Set<Road> roads = roadFunc.apply(sector);

        Set<Junction> junctions = Sets.newHashSet();
        
        for (Road road : roads) {

            junctions.add(road.getStart());
            junctions.add(road.getEnd());
            
            Shape shape = getRoadShape(road);

            if (!hitClip(sector, shape)) {
                continue;
            }

            allPaths.append(shape, false);
        }

        for (Junction junction : junctions) {
            Shape plaza = createPlaza(junction.getCoords(), 10.0);
            allPaths.append(plaza, false);
        }
        
        return allPaths;
    }
    
    private Shape getRoadShape(Road road) {
        List<Point2i> pts = Lists.newArrayList(road.getPoints());

        pts.add(0, road.getStart().getCoords());
        pts.add(road.getEnd().getCoords());
        Path2D path = Splines.getBezierSplinePath(pts, 0.2);

        float strokeWidth = (float) road.getWidth() * 3f;
            
        int cap = BasicStroke.CAP_ROUND;    // end of path
        int join = BasicStroke.JOIN_ROUND;  // connected path segments
        BasicStroke thick = new BasicStroke(strokeWidth, cap, join);

        Shape shape = thick.createStrokedShape(path);

        return shape;
    }

    private boolean hitClip(Sector sector, Shape shape) {
        Rectangle shapeBounds = shape.getBounds();

        Point2i coords = sector.getCoords();
        int bx = coords.x * Sector.SIZE;
        int bz = coords.y * Sector.SIZE;
        Rectangle secRect = new Rectangle(bx, bz, Sector.SIZE, Sector.SIZE);
        
        return shapeBounds.intersects(secRect);
    }

    private Shape createPlaza(Point2i pos, double radius) {
        double x = pos.x - radius * 0.5;
        double y = pos.y - radius * 0.5;

        return new Area(new Ellipse2D.Double(x, y, radius, radius));
    }

}
