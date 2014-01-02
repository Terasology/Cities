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
import org.terasology.math.TeraMath;

/**
 * A cache that stores a rectangular area and interpolates values bi-linearly
 * @author Martin Steiger
 */
class CachingLerpHeightMap extends HeightMapAdapter {

    private static final Logger logger = LoggerFactory.getLogger(CachingLerpHeightMap.class);
    
    private final short[] height;
    private final Rectangle area;
    private final HeightMap hm;
    private final int scale;
    private final int scaledWidth;
    private final int scaledHeight;

    /**
     * @param area the area to cache
     * @param hm the height map to use
     * @param scale the scale level (should be a divisor of area.width and area.height)
     */
    public CachingLerpHeightMap(Rectangle area, HeightMap hm, int scale) {
        this.area = area;
        this.scale = scale;
        this.hm = hm;
        
        this.scaledWidth = area.width / scale + 1;
        this.scaledHeight = area.height / scale + 1;

        this.height = new short[scaledWidth * scaledHeight];
        
        // area is 1 larger 
        for (int z = 0; z < scaledHeight; z++) {
            for (int x = 0; x < scaledWidth; x++) {
                int y = hm.apply(area.x + x * scale, area.y + z * scale);
                height[z * scaledWidth + x] = (short) y;
            }
        }
    }
    
    @Override
    public int apply(int x, int z) {
        boolean xOk = (x >= area.x) && (x < area.x + area.width);
        boolean zOk = (z >= area.y) && (z < area.y + area.height);
        
        if (xOk && zOk) {
            double lx = (x - area.x) / (double) scale;
            double lz = (z - area.y) / (double) scale;

            int minX = TeraMath.floorToInt(lx);
            int maxX = minX + 1;

            int minZ = TeraMath.floorToInt(lz);
            int maxZ = minZ + 1;

            int q00 = getHeight(minX, minZ);
            int q10 = getHeight(maxX, minZ);
            int q01 = getHeight(minX, maxZ);
            int q11 = getHeight(maxX, maxZ);

            double ipx = lx - minX;
            double ipz = lz - minZ;

            double min = TeraMath.lerp(q00, q10, ipx);
            double max = TeraMath.lerp(q01, q11, ipx);

            double res = TeraMath.lerp(min, max, ipz);

            return TeraMath.floorToInt(res + 0.5);
        }
        
        logger.debug("Accessing height map outside cached bounds -- referring to uncached height map");
        
        return hm.apply(x, z);
    }
    
    private int getHeight(int lx, int lz) {
        return height[lz * scaledWidth + lx];
    }

}
