/*
 * Copyright 2014 MovingBlocks
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
import java.util.List;

/**
 * Provides access to different height maps
 * @author Martin Steiger
 */
public final class HeightMaps {
    
    private HeightMaps() {
        // avoid instantiation
    }
    
    /**
     * @param hm the height to use
     * @param area the area to cache
     * @param scale the scale level (should be a divisor of area.width and area.height)
     * @return An height map based on the given constant value 
     */
    public static HeightMap caching(HeightMap hm, Rectangle area, int scale) {
        if (scale == 1) {
            return new CachingHeightMap(area, hm);
        } else {
            return new CachingLerpHeightMap(area, hm, scale);
        }
    }
    
    /**
     * @param height the height to use
     * @return An height map based on the given constant value 
     */
    public static HeightMap constant(int height) {
        return new ConstantHeightMap(height);
    }
    
    /**
     * @param hm the backing height map
     * @param offset the height offset to use
     * @return An height map that returns all values with +offset 
     */
    public static HeightMap offset(HeightMap hm, int offset) {
        return new OffsetHeightMap(hm, offset);
    }
    
    /**
     * @param hm the backing height map
     * @param scale the scale factor to use
     * @return An height map that returns values at (x * scale, z * scale) 
     */
    public static HeightMap scalingArea(final HeightMap hm, final int scale) {
        return new HeightMapAdapter() {

            @Override
            public int apply(int x, int z) {
                return hm.apply(x * scale, z * scale);
            }
        };
    }
    
    /**
     * @param hm the backing height map
     * @param scale the scale factor to use
     * @return An height map that returns y * scale 
     */
    public static HeightMap scalingHeight(final HeightMap hm, final int scale) {
        return new HeightMapAdapter() {

            @Override
            public int apply(int x, int z) {
                return hm.apply(x, z) * scale;
            }
        };
    }
    
    /**
     * @param data the data
     * @return An height map that returns the content of the string list 
     */
    public static HeightMap stringBased(List<String> data) {
        return new StringHeightMap(data);
    }

    /**
     * @param hm the height map
     * @return a height map that is mirror along the diagonal (1, -1)
     */
    public static HeightMap symmetricAlongZ(final HeightMap hm) {
        return new HeightMapAdapter() {

            @Override
            public int apply(int x, int z) {
                if (x < 0) {
                    return hm.apply(-x, z);
                }
                return hm.apply(x, z);
            }
        };
    }

    /**
     * @param hm the height map
     * @return a height map that is mirror along the diagonal (1, -1)
     */
    public static HeightMap symmetricAlongX(final HeightMap hm) {
        return new HeightMapAdapter() {

            @Override
            public int apply(int x, int z) {
                if (z < 0) {
                    return hm.apply(x, -z);
                }
                return hm.apply(x, z);
            }
        };
    }

    /**
     * @param hm the height map
     * @return a height map that is mirror along the diagonal (1, -1)
     */
    public static HeightMap symmetricAlongDiagonal(final HeightMap hm) {
        return new HeightMapAdapter() {

            @Override
            public int apply(int x, int z) {
                if (x + z < 0) {
                    int dist = x + z;
                    return hm.apply(x - dist, z - dist);
                }
                return hm.apply(x, z);
            }
        };
    }
}
