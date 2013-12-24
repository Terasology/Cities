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
import org.terasology.cities.model.SimpleDoor;
import org.terasology.cities.model.SimpleHome;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.RasterRegistry;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.raster.TerrainInfo;
import org.terasology.cities.terrain.OffsetHeightMap;

/**
 * Converts a {@link SimpleHome} into blocks
 * @author Martin Steiger
 */
public class SimpleHomeRasterizer implements Rasterizer<SimpleHome> {

    @Override
    public void raster(Brush brush, TerrainInfo ti, SimpleHome blg) {
        Rectangle rc = blg.getLayout();
        
        if (brush.affects(rc)) {
        
            int baseHeight = blg.getBaseHeight();
            int wallHeight = blg.getWallHeight();
    
            // clear area above floor level
            brush.fillRect(rc, baseHeight, new OffsetHeightMap(ti.getHeightMap(), 1), BlockTypes.AIR);

            // lay floor level
            brush.fillRect(rc, baseHeight - 1, baseHeight, BlockTypes.BUILDING_FLOOR);

            // put foundation concrete below 
            brush.fillRect(rc, ti.getHeightMap(), baseHeight - 1, BlockTypes.BUILDING_FOUNDATION);
            
            // wall along z
            brush.frame(rc, baseHeight, blg.getBaseHeight() + wallHeight, BlockTypes.BUILDING_WALL);
    
            // door
            SimpleDoor door = blg.getDoor();
            brush.fillRect(door.getRect(), door.getBaseHeight(), door.getTopHeight(), BlockTypes.AIR);
        }
        
        // the roof can be larger than the building -- not sure if this is a good idea ...
        
        RasterRegistry registry = StandardRegistry.getInstance();
        registry.rasterize(brush, ti, blg.getRoof());
    }

}
