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

package org.terasology.cities.terrain;

import java.awt.Rectangle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A cache that stores a rectangular area
 * @author Martin Steiger
 */
public class CachingHeightMap extends HeightMapAdapter {

    private static final Logger logger = LoggerFactory.getLogger(CachingHeightMap.class);
    
    private final int[] height;
    private final Rectangle area;
    private final HeightMap hm;

    /**
     * @param area the area to cache
     * @param hm the height map to use
     */
    public CachingHeightMap(Rectangle area, HeightMap hm) {
        this.area = area;
        this.hm = hm;
        this.height = new int[area.width * area.height];
        
        for (int z = 0; z < area.height; z++) {
            for (int x = 0; x < area.width; x++) {
                height[z * area.width + x] = hm.apply(x + area.x, z + area.y);
            }
        }
    }
    
    @Override
    public int apply(int x, int z) {
        if ((x >= area.x && x < area.x + area.width)
         && (z >= area.y && z < area.y + area.height)) {
            int lx = x - area.x;
            int lz = z - area.y;
            return height[lz * area.width + lx];
        }
        
        logger.debug("Accessing height map outside cached bounds -- referring to uncached height map");
        
        return hm.apply(x, z);
    }

}
