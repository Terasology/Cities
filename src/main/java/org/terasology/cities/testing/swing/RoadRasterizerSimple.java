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

package org.terasology.cities.testing.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Set;

import javax.vecmath.Point2i;

import org.terasology.cities.model.Junction;
import org.terasology.cities.model.Road;

/**
 * Renders roads using straight line segments
 * @author Martin Steiger
 */
public class RoadRasterizerSimple {

    /**
     * @param g the graphics object
     * @param roads the roads to draw
     */
    public void rasterRoads(Graphics2D g, Set<Road> roads) {

        
        for (Road road : roads) {
            
            List<Point2i> pts = road.getPoints();

            Point2i prev = road.getStart().getCoords();
            
            float width = (float) road.getWidth();
            g.setStroke(new BasicStroke(width));
            g.setColor(Color.BLACK);
           
            for (int idx = 0; idx < pts.size(); idx++) {
                Point2i pt = pts.get(idx);
            
                drawSegment(g, prev, pt);
                drawSegmentEnd(g, pt);
                
                prev = pt;
            }
            
            drawSegment(g, prev, road.getEnd().getCoords());

            g.setStroke(new BasicStroke());
            
            drawJunction(g, road.getStart());
            drawJunction(g, road.getEnd());
        }
    }

    private void drawJunction(Graphics2D g, Junction jun) {
        Point2i pos = jun.getCoords();
        
        int r = 3;
        g.setColor(Color.BLACK);
        g.drawRect(pos.x - r, pos.y - r, 2 * r, 2 * r);
        g.setColor(Color.BLUE);
        g.drawString("" + jun.getRoads().size(), pos.x + 5, pos.y);
    }

    private void drawSegmentEnd(Graphics2D g, Point2i pos) {
        
        int r = 2;
        g.drawOval(pos.x - r, pos.y - r, 2 * r, 2 * r);
    }

    private void drawSegment(Graphics2D g, Point2i from, Point2i to) {
        
        g.setColor(Color.BLACK);
        g.drawLine(from.x, from.y, to.x, to.y);

    }
}
