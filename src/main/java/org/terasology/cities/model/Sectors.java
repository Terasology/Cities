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

import java.math.RoundingMode;
import java.util.Map;

import javax.vecmath.Point2i;

import com.google.common.collect.Maps;
import com.google.common.math.IntMath;

/**
 * Gives access to all Sectors
 * @author Martin Steiger
 */
public final class Sectors {
    
    private static final Map<Point2i, Sector> SECTORS = Maps.newConcurrentMap();
    
    private Sectors() {
        // private
    }

    /**
     * @param x the x coordinate of the sector
     * @param z the z coordinate of the sector
     * @return the sector
     */
    public static Sector getSector(int x, int z) {
        return getSector(new Point2i(x, z));
    }

    /**
     * @param coord the coordinate of the sector
     * @return the sector
     */
    public static Sector getSector(Point2i coord) {
        Sector sector = SECTORS.get(coord);
        
        if (sector == null) {
            sector = new Sector(coord);
            
            SECTORS.put(coord, sector);
        }
        
        return sector;
    }

    /**
     * @param wx the world block x coord
     * @param wz the world block z coord
     * @return the sector
     */
    public static Sector getSectorForBlock(int wx, int wz) {
        int sx = IntMath.divide(wx, Sector.SIZE, RoundingMode.FLOOR);
        int sz = IntMath.divide(wz, Sector.SIZE, RoundingMode.FLOOR);
        
        return getSector(new Point2i(sx, sz));
    }
}
