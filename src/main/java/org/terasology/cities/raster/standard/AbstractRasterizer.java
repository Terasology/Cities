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

package org.terasology.cities.raster.standard;

import java.awt.Rectangle;

import org.terasology.cities.BlockTypes;
import org.terasology.cities.heightmap.HeightMap;
import org.terasology.cities.heightmap.HeightMaps;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.Rasterizer;

/**
 * Provides several often-used methods
 * @param <T> the element type
 * @author Martin Steiger
 */
public abstract class AbstractRasterizer<T> implements Rasterizer<T> {

    /**
     * @param brush the brush to use
     * @param rc the rectangle to prepare
     * @param terrain the terrain height map
     * @param baseHeight the floor level
     * @param floor the floor block type
     */
    protected void prepareFloor(Brush brush, Rectangle rc, HeightMap terrain, int baseHeight, BlockTypes floor) {
        
        // clear area above floor level
        brush.fillRect(rc, baseHeight, HeightMaps.offset(terrain, 1), BlockTypes.AIR);

        // lay floor level
        brush.fillRect(rc, baseHeight - 1, baseHeight, floor);

        // put foundation concrete below 
        brush.fillRect(rc, terrain, baseHeight - 1, BlockTypes.BUILDING_FOUNDATION);
        
    }
}
