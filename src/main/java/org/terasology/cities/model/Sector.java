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
 * Defines a square-shaped terrain sector
 * @author Martin Steiger
 */
public final class Sector {
    
    @SuppressWarnings("javadoc")
    public enum Orientation {
        NORTH(0),
        NORTHEAST(1),
        EAST(2),
        SOUTHEAST(3),
        SOUTH(4),
        SOUTHWEST(5),
        WEST(6),
        NORTHWEST(7);
        
        private final int index;
        
        Orientation(int index) {
            this.index = index;
        }
        
        /**
         * @return the array index in the orientation array
         */
        public int getIndex() {
            return index;
        }
    }
    
    /**
     * Measured in blocks
     */
    public static final int SIZE = 1024;
    
    private final Vector2i coords;

    /**
     * @param coords the coordinates
     */
    Sector(Vector2i coords) {
        if (coords == null) {
            throw new NullPointerException("coords cannot be null");
        }
        
        this.coords = coords;
    }

    /**
     * @return the coordinates
     */
    public Vector2i getCoords() {
        return coords;
    }
    
    /**
     * @param dir the orientation which gives the neighbor index in [0..8]
     * @return the neighbor sector
     */
    public Sector getNeighbor(Orientation dir) {
        int[] ox = new int[] {0, 1, 1, 1, 0, -1, -1, -1 };
        int[] oz = new int[] {-1, -1, 0, 1, 1, 1, 0, -1 };
        
        int index = dir.getIndex();
        int x = coords.x + ox[index];
        int z = coords.y + oz[index];
        
        return Sectors.getSector(new Vector2i(x, z));
    }

    @Override
    public String toString() {
        return "Sector [" + coords.x + ", " + coords.y + "]";
    }

    @Override
    public int hashCode() {
        return coords.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        Sector other = (Sector) obj;

        // coords cannot be null
        return coords.equals(other.coords);
    }

    
    
}

