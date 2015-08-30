/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.cities.generator;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

import org.terasology.cities.model.bldg.Tower;
import org.terasology.cities.model.bldg.TownWall;
import org.terasology.cities.model.bldg.WallSegment;
import org.terasology.math.geom.BaseVector2i;

/**
 * Computes the blocked area of a given {@link TownWall}
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
            BaseVector2i start = ws.getStart();
            BaseVector2i end = ws.getEnd();

            Shape line = new Line2D.Double(start.getX(), start.getY(), end.getX(), end.getY());

            BasicStroke thick = new BasicStroke(ws.getWallThickness());

            line = thick.createStrokedShape(line);

            shape.append(line, false);
        }

        return shape;
    }
}
