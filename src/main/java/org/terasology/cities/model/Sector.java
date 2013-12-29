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

package org.terasology.cities.model;

import javax.vecmath.Point2i;

import org.terasology.cities.common.Orientation;
import org.terasology.math.Vector2i;

/**
 * Defines a square-shaped terrain sector
 * @author Martin Steiger
 */
public final class Sector {
    
    /**
     * Measured in blocks
     */
    public static final int SIZE = 1024;
    
    private final Point2i coords;

    /**
     * @param coords the coordinates
     */
    Sector(Point2i coords) {
        if (coords == null) {
            throw new NullPointerException("coords cannot be null");
        }
        
        this.coords = coords;
    }

    /**
     * @return the coordinates
     */
    public Point2i getCoords() {
        return coords;
    }
    
    /**
     * @param dir the orientation which gives the neighbor index in [0..8]
     * @return the neighbor sector
     */
    public Sector getNeighbor(Orientation dir) {
        
        Vector2i v = dir.getDir();
        int x = coords.x + v.x;
        int z = coords.y + v.y;
        
        return Sectors.getSector(new Point2i(x, z));
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

