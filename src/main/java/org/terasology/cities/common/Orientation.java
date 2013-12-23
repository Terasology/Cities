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

package org.terasology.cities.common;

import java.util.EnumMap;

import org.terasology.math.Vector2i;

import com.google.common.collect.Maps;

/**
 * Defines orientation
 * @author Martin Steiger
 */
public enum Orientation {
    
    /**
     * North 
     */
    NORTH(0, -1),
    
    /**
     * North-East 
     */
    NORTHEAST(1, -1),
    
    /**
     * East 
     */
    EAST(1, 0),
    
    /**
     * South-East 
     */
    SOUTHEAST(1, 1),
    
    /**
     * South
     */
    SOUTH(0, 1),
    
    /**
     * South-West 
     */
    SOUTHWEST(-1, 1),
    
    /**
     * West 
     */
    WEST(-1, 0),
    
    /**
     * North-West 
     */
    NORTHWEST(-1, -1);
    
    private static final EnumMap<Orientation, Orientation> OPPOSITES = Maps.newEnumMap(Orientation.class);

    static {
        OPPOSITES.put(NORTH, SOUTH);
        OPPOSITES.put(NORTHEAST, SOUTHWEST);
        OPPOSITES.put(EAST, WEST);
        OPPOSITES.put(SOUTHEAST, NORTHWEST);
        OPPOSITES.put(SOUTH, NORTH);
        OPPOSITES.put(SOUTHWEST, NORTHEAST);
        OPPOSITES.put(WEST, EAST);
        OPPOSITES.put(NORTHWEST, SOUTHEAST);
    }
    
    private final Vector2i dir;

    Orientation(int dx, int dz) {
        this.dir = new Vector2i(dx, dz);
    }

    /**
     * @return the opposite orientation
     */
    public Orientation getOpposite() {
        return OPPOSITES.get(this);
    }
    
    /**
     * @return the orientation
     */
    public Vector2i getDir() {
        return this.dir;
    }
}

