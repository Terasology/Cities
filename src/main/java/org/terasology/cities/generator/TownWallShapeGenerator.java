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
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

import org.terasology.cities.model.Tower;
import org.terasology.cities.model.TownWall;
import org.terasology.cities.model.WallSegment;
import org.terasology.math.Vector2i;

/**
 * Computes the blocked area of a given {@link TownWall}
 * @author Martin Steiger
 */
public class TownWallShapeGenerator {

    /**
     * @param tw the town wall
     * @return the shape the town wall blocks
     */
    public Shape computeShape(TownWall tw) {
        
        Path2D shape = new Path2D.Double();
        
        for (Tower tower : tw.getTowers()) {
            shape.append(tower.getLayout(), false);
        }

        for (WallSegment ws : tw.getWalls()) {
            Vector2i start = ws.getStart();
            Vector2i end = ws.getEnd();
            
            Shape line = new Line2D.Double(start.x, start.y, end.x, end.y);
            
            BasicStroke thick = new BasicStroke(ws.getWallThickness());

            line = thick.createStrokedShape(line);

            shape.append(line, false);
        }
        
        return shape;
    }
}
