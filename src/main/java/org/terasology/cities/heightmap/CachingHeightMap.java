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

package org.terasology.cities.heightmap;

import java.awt.Rectangle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A cache that stores a rectangular area
 * @author Martin Steiger
 */
class CachingHeightMap extends HeightMapAdapter {

    private static final Logger logger = LoggerFactory.getLogger(CachingHeightMap.class);
    
    private final short[] height;
    private final Rectangle area;
    private final HeightMap hm;

    /**
     * @param area the area to cache
     * @param hm the height map to use
     */
    public CachingHeightMap(Rectangle area, HeightMap hm) {
        this.area = area;
        this.hm = hm;
        this.height = new short[area.width * area.height];
        
        for (int z = 0; z < area.height; z++) {
            for (int x = 0; x < area.width; x++) {
                int y = hm.apply(x + area.x, z + area.y);
                height[z * area.width + x] = (short) y;
            }
        }
    }
    
    @Override
    public int apply(int x, int z) {
        boolean xOk = x >= area.x && x < area.x + area.width;
        boolean zOk = z >= area.y && z < area.y + area.height;
        
        if (xOk && zOk) {
            int lx = x - area.x;
            int lz = z - area.y;
            return height[lz * area.width + lx];
        }
        
        logger.debug("Accessing height map outside cached bounds -- referring to uncached height map");
        
        return hm.apply(x, z);
    }

}
