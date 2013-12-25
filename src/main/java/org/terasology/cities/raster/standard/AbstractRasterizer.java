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

package org.terasology.cities.raster.standard;

import java.awt.Rectangle;

import org.terasology.cities.BlockTypes;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.terrain.HeightMap;
import org.terasology.cities.terrain.OffsetHeightMap;

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
        brush.fillRect(rc, baseHeight, new OffsetHeightMap(terrain, 1), BlockTypes.AIR);

        // lay floor level
        brush.fillRect(rc, baseHeight - 1, baseHeight, floor);

        // put foundation concrete below 
        brush.fillRect(rc, terrain, baseHeight - 1, BlockTypes.BUILDING_FOUNDATION);
        
    }
}
