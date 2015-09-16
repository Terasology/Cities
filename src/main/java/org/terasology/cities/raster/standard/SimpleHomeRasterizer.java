/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import org.terasology.cities.bldg.SimpleDoor;
import org.terasology.cities.bldg.Window;
import org.terasology.cities.model.bldg.SimpleHome;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.RasterRegistry;
import org.terasology.cities.raster.TerrainInfo;

/**
 * Converts a {@link SimpleHome} into blocks
 */
public class SimpleHomeRasterizer extends AbstractRasterizer<SimpleHome> {

    @Override
    public void raster(Brush brush, TerrainInfo ti, SimpleHome blg) {
        Rectangle rc = blg.getLayout();
        
        RasterRegistry registry = StandardRegistry.getInstance();

        if (brush.affects(rc)) {
        
            int baseHeight = blg.getBaseHeight();
            int wallHeight = blg.getWallHeight();
    
            prepareFloor(brush, rc, ti.getHeightMap(), baseHeight, BlockTypes.BUILDING_FLOOR);
            
            // create walls
            brush.frame(rc, baseHeight, blg.getBaseHeight() + wallHeight, BlockTypes.BUILDING_WALL);
    
            // door
            SimpleDoor door = blg.getDoor();
            brush.fillRect(door.getRect(), door.getBaseHeight(), door.getTopHeight(), BlockTypes.AIR);

            // windows
            for (Window wnd : blg.getWindows()) {
                registry.rasterize(brush, ti, wnd);
            }
        }
        
        // the roof can be larger than the building -- not sure if this is a good idea ...
        
        registry.rasterize(brush, ti, blg.getRoof());
    }

}
