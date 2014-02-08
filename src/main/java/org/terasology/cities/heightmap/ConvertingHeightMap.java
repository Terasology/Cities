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

package org.terasology.cities.heightmap;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Converts height map data w.r.t. a map-based conversion
 * @author Martin Steiger
 */
public class ConvertingHeightMap extends HeightMapAdapter {

    private final Map<Integer, Integer> converter = Maps.newHashMap();

    private final HeightMap heightMap;
    
    /**
     * @param heightMap the base height map
     */
    public ConvertingHeightMap(HeightMap heightMap) {
        this.heightMap = heightMap;
    }
    
    /**
     * Adds a conversion from -> to
     * @param from from
     * @param to to
     */
    public void addConversion(Integer from, Integer to) {
        converter.put(from, to);
    }

    @Override
    public int apply(int x, int z) {
        int val = heightMap.apply(x, z);
        Integer result = converter.get(val);
        
        if (result != null) {
            return result;
        }
        
        return val;
    }
}
