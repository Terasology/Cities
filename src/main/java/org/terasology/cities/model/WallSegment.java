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

package org.terasology.cities.model;

import org.terasology.math.Vector2i;

/**
 * A straight wall segment
 * @author Martin Steiger
 */
public class WallSegment {

    private final Vector2i start;
    private final Vector2i end;
    
    private int wallThickness;

    /**
     * @param start one end of the wall
     * @param end the other end of the wall
     * @param wallThickness the wall thickness in block
     */
    public WallSegment(Vector2i start, Vector2i end, int wallThickness) {
        this.start = start;
        this.end = end;
        this.wallThickness = wallThickness;
    }

    /**
     * @return one end of the wall
     */
    public Vector2i getStart() {
        return this.start;
    }

    /**
     * @return the other end of the wall
     */
    public Vector2i getEnd() {
        return this.end;
    }

    /**
     * @return the wallThickness
     */
    public int getWallThickness() {
        return this.wallThickness;
    }

}
