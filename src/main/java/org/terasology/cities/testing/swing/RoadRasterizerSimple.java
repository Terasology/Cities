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

import javax.vecmath.Point2d;

import org.terasology.cities.model.Junction;
import org.terasology.cities.model.Road;
import org.terasology.cities.model.Sector;

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
            
            List<Point2d> pts = road.getPoints();

            Point2d prev = road.getStart().getCoords();
            
            float width = (float) road.getWidth();
            g.setStroke(new BasicStroke(width));
            g.setColor(Color.BLACK);
           
            for (int idx = 0; idx < pts.size(); idx++) {
                Point2d pt = pts.get(idx);
            
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
        Point2d pos = jun.getCoords();
        
        int x = (int) ((pos.x) * Sector.SIZE);
        int y = (int) ((pos.y) * Sector.SIZE);
        
        int r = 3;
        g.setColor(Color.BLACK);
        g.drawRect(x - r, y - r, 2 * r, 2 * r);
        g.setColor(Color.BLUE);
        g.drawString("" + jun.getRoads().size(), x + 5, y);
    }

    private void drawSegmentEnd(Graphics2D g, Point2d pos) {
        int x = (int) ((pos.x) * Sector.SIZE);
        int y = (int) ((pos.y) * Sector.SIZE);
        
        int r = 2;
        g.drawOval(x - r, y - r, 2 * r, 2 * r);
    }

    private void drawSegment(Graphics2D g, Point2d from, Point2d to) {
        int x1 = (int) ((from.x) * Sector.SIZE);
        int y1 = (int) ((from.y) * Sector.SIZE);
        int x2 = (int) ((to.x) * Sector.SIZE);
        int y2 = (int) ((to.y) * Sector.SIZE);
        
        g.setColor(Color.BLACK);
        g.drawLine(x1, y1, x2, y2);

    }
}
