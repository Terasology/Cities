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
