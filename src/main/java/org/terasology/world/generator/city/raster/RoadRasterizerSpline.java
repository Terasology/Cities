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

package org.terasology.world.generator.city.raster;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.Set;

import javax.vecmath.Point2d;

import org.terasology.common.Splines;
import org.terasology.math.Vector2i;
import org.terasology.world.generator.city.model.Junction;
import org.terasology.world.generator.city.model.Road;
import org.terasology.world.generator.city.model.Sector;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Renders roads using splines that pass though all segment points
 * @author Martin Steiger
 */
public class RoadRasterizerSpline {

    /**
     * @param sector the rendered sector
     * @param roads the roads to draw
     * @return the area that contains all roads 
     */
    public Area getRoadArea(Sector sector, Set<Road> roads) {

        Area allAreas = new Area();

        Set<Junction> junctions = Sets.newHashSet();
        
        for (Road road : roads) {

            junctions.add(road.getStart());
            junctions.add(road.getEnd());
            
            Shape shape = getRoadShape(road);

            if (!hitClip(sector, shape)) {
                continue;
            }

            Area area = new Area(shape);
            allAreas.add(area);
        }

        for (Junction junction : junctions) {
            Area plaza = createPlaza(junction.getCoords(), 10.0);
            allAreas.add(plaza);
        }
        
        return allAreas;
    }
    
    /**
     * @param g the graphics object
     * @param allAreas the area to draw
     */
    public void rasterRoadArea(Graphics2D g, Shape allAreas) {

        g.setStroke(new BasicStroke());
        g.setColor(Color.BLACK);
        
        g.draw(allAreas);

    }
    
    private Shape getRoadShape(Road road) {
        List<Point2d> pts = Lists.newArrayList(road.getPoints());

        pts.add(0, road.getStart().getCoords());
        pts.add(road.getEnd().getCoords());
        Path2D path = Splines.getBezierSplinePath(scale(pts), 0.2);

        float strokeWidth = (float) road.getWidth() * 3f;
            
        int cap = BasicStroke.CAP_ROUND;    // end of path
        int join = BasicStroke.JOIN_ROUND;  // connected path segments
        BasicStroke thick = new BasicStroke(strokeWidth, cap, join);

        Shape shape = thick.createStrokedShape(path);

        return shape;
    }

    private boolean hitClip(Sector sector, Shape shape) {
        Rectangle shapeBounds = shape.getBounds();

        Vector2i coords = sector.getCoords();
        int bx = coords.x * Sector.SIZE;
        int bz = coords.y * Sector.SIZE;
        Rectangle secRect = new Rectangle(bx, bz, Sector.SIZE, Sector.SIZE);
        
        return shapeBounds.intersects(secRect);
    }

    private Area createPlaza(Point2d pos, double radius) {
        double x = (pos.x) * Sector.SIZE - radius * 0.5;
        double y = (pos.y) * Sector.SIZE - radius * 0.5;

        return new Area(new Ellipse2D.Double(x, y, radius, radius));
    }

    private List<Point2d> scale(List<Point2d> pts) {
        List<Point2d> result = Lists.newArrayList();
        
        for (Point2d pt : pts) {
            Point2d p2 = new Point2d(pt);
            p2.scale(Sector.SIZE);
            result.add(p2);
        }
        return result;
    }

}
