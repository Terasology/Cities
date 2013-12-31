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
    private final int scale;

    /**
     * @param area the area to cache
     * @param hm the height map to use
     * @param scale the scale level (should be a divisor of area.width and area.height)
     */
    public CachingHeightMap(Rectangle area, HeightMap hm, int scale) {
        this.area = area;
        this.scale = scale;
        this.hm = hm;
        this.height = new int[area.width * area.height / (scale * scale)];
        
        for (int z = 0; z < area.height / scale; z++) {
            for (int x = 0; x < area.width / scale; x++) {
                height[z * area.width / scale + x] = hm.apply(x * scale + area.x, z * scale + area.y);
            }
        }
    }
    
    @Override
    public int apply(int x, int z) {
        boolean xOk = x >= area.x && x < area.x + area.width;
        boolean zOk = z >= area.y && z < area.y + area.height;
        
        if (xOk && zOk) {
            int lx = (x - area.x) / scale;
            int lz = (z - area.y) / scale;
            return height[lz * area.width / scale + lx];
        }
        
        logger.debug("Accessing height map outside cached bounds -- referring to uncached height map");
        
        return hm.apply(x, z);
    }

}
