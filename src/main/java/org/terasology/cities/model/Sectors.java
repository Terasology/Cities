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

import java.math.RoundingMode;
import java.util.Map;

import javax.vecmath.Point2i;
import javax.vecmath.Vector3f;

import org.terasology.math.TeraMath;

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
     * @param pos the world position
     * @return the sector
     */
    public static Sector getSectorForPosition(Vector3f pos) {
        return getSectorForBlock(TeraMath.floorToInt(pos.x), TeraMath.floorToInt(pos.z));
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
