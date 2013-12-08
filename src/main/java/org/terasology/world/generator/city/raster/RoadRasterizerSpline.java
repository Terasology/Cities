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
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.Set;

import javax.vecmath.Point2d;

import org.terasology.common.Splines;
import org.terasology.world.generator.city.model.Road;
import org.terasology.world.generator.city.model.Sector;

import com.google.common.collect.Lists;

/**
 * Renders roads using splines that pass though all segment points
 * @author Martin Steiger
 */
public class RoadRasterizerSpline {

    /**
     * @param g the graphics object
     * @param roads the roads to draw
     */
    public void rasterRoads(Graphics2D g, Set<Road> roads) {

        g.setColor(Color.BLACK);

        Area allAreas = new Area();

        for (Road road : roads) {

            Area start = createPlaza(road.getStart().getCoords(), 10.0);
            Area end = createPlaza(road.getEnd().getCoords(), 10.0);

            List<Point2d> pts = Lists.newArrayList(road.getPoints());

            pts.add(0, road.getStart().getCoords());
            pts.add(road.getEnd().getCoords());
            Path2D path = Splines.getBezierSplinePath(scale(pts), 0.2);

            float strokeWidth = (float) road.getWidth() * 3f;
            int cap = BasicStroke.CAP_ROUND;    // end of path
            int join = BasicStroke.JOIN_ROUND;  // connected path segments
            BasicStroke thick = new BasicStroke(strokeWidth, cap, join);

            Shape shape = thick.createStrokedShape(path);
            Area area = new Area(shape);

            allAreas.add(area);
            allAreas.add(start);
            allAreas.add(end);
            
        }

        g.draw(allAreas);

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
