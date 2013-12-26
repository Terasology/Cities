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

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 * Defines orientation in a cartographic sense
 * @author Martin Steiger
 */
public enum Orientation {
    
    /**
     * North (0, -1)
     */
    NORTH(0, -1),
    
    /**
     * North-East (1, -1)
     */
    NORTHEAST(1, -1),
    
    /**
     * East (1, 0)
     */
    EAST(1, 0),
    
    /**
     * South-East (1, 1) 
     */
    SOUTHEAST(1, 1),
    
    /**
     * South (0, 1)
     */
    SOUTH(0, 1),
    
    /**
     * South-West (-1, 1)
     */
    SOUTHWEST(-1, 1),
    
    /**
     * West (-1, 0)
     */
    WEST(-1, 0),
    
    /**
     * North-West (-1, -1)
     */
    NORTHWEST(-1, -1);
    
    private static final EnumMap<Orientation, Orientation> OPPOSITES = Maps.newEnumMap(Orientation.class);
    private static final EnumMap<Orientation, Orientation> ROTATE_CW = Maps.newEnumMap(Orientation.class);
    private static final EnumMap<Orientation, Orientation> ROTATE_CCW = Maps.newEnumMap(Orientation.class);

    static {
        OPPOSITES.put(NORTH, SOUTH);
        OPPOSITES.put(NORTHEAST, SOUTHWEST);
        OPPOSITES.put(EAST, WEST);
        OPPOSITES.put(SOUTHEAST, NORTHWEST);
        OPPOSITES.put(SOUTH, NORTH);
        OPPOSITES.put(SOUTHWEST, NORTHEAST);
        OPPOSITES.put(WEST, EAST);
        OPPOSITES.put(NORTHWEST, SOUTHEAST);

        ROTATE_CW.put(NORTH, NORTHEAST);
        ROTATE_CW.put(NORTHEAST, EAST);
        ROTATE_CW.put(EAST, SOUTHEAST);
        ROTATE_CW.put(SOUTHEAST, SOUTH);
        ROTATE_CW.put(SOUTH, SOUTHWEST);
        ROTATE_CW.put(SOUTHWEST, WEST);
        ROTATE_CW.put(WEST, NORTHWEST);
        ROTATE_CW.put(NORTHWEST, NORTH);

        ROTATE_CCW.put(NORTH, NORTHWEST);
        ROTATE_CCW.put(NORTHWEST, WEST);
        ROTATE_CCW.put(WEST, SOUTHWEST);
        ROTATE_CCW.put(SOUTHWEST, SOUTH);
        ROTATE_CCW.put(SOUTH, SOUTHEAST);
        ROTATE_CCW.put(SOUTHEAST, EAST);
        ROTATE_CCW.put(EAST, NORTHEAST);
        ROTATE_CCW.put(NORTHEAST, NORTH);
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
     * @param degrees the rotation in degrees at 45° intervals (clockwise)
     * @return the orientation
     */
    public Orientation getRotated(int degrees) {
        Preconditions.checkArgument(degrees % 45 == 0, "degrees must be a multiple of 45 (is %s)", degrees);

        // normalize degrees to be in [0..360[
        int norm = (degrees % 360);
        if (norm < 0) {
            norm += 360;
        }
        
        if (norm == 0) {
            return this;
        }
        
        if (norm == 180) {
            return OPPOSITES.get(this);
        }

        Orientation result = this;

        // this requires 3 map lookups maximum
        
        while (norm < 180 && norm > 0) {
            result = ROTATE_CW.get(result);
            norm -= 45;
        }
        
        while (norm > 180 && norm < 360) {
            result = ROTATE_CCW.get(result);
            norm += 45;
        }
        
        return result;
    }
    
    /**
     * @return the orientation
     */
    public Vector2i getDir() {
        return this.dir;
    }
}

