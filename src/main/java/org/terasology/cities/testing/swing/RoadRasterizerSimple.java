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
