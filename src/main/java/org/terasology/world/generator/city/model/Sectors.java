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

package org.terasology.world.generator.city.model;

import java.util.Map;

import org.terasology.math.Vector2i;

import com.google.common.collect.Maps;

/**
 * Gives access to all Sectors
 * @author Martin Steiger
 */
public final class Sectors {
    
    private static final Map<Vector2i, Sector> SECTORS = Maps.newConcurrentMap();
    
    private Sectors() {
        // private
    }
    
    /**
     * @param coord the coordinate of the sector
     * @return the sector
     */
    public static Sector getSector(Vector2i coord) {
        Sector sector = SECTORS.get(coord);
        
        if (sector == null) {
            sector = new Sector(coord);
            
            SECTORS.put(coord, sector);
        }
        
        return sector;
    }
}
