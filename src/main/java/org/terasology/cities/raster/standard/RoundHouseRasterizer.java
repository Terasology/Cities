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

package org.terasology.cities.raster.standard;

import java.awt.geom.Ellipse2D;

import org.terasology.cities.BlockTypes;
import org.terasology.cities.model.bldg.RoundHouse;
import org.terasology.cities.model.bldg.SimpleDoor;
import org.terasology.cities.model.bldg.Window;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.Pen;
import org.terasology.cities.raster.RasterRegistry;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.raster.TerrainInfo;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.commonworld.heightmap.HeightMaps;

/**
 * Converts a {@link RoundHouse} into blocks
 * @author Martin Steiger
 */
public class RoundHouseRasterizer implements Rasterizer<RoundHouse> {
    
    @Override
    public void raster(final Brush brush, TerrainInfo ti, final RoundHouse house) {

        // TODO: find out why ellipse doesn't work properly -- rounding errors?
        final Ellipse2D area = house.getLayout();

//        if (!brush.affects(area)) {
//            return;
//        }
        
        final int centerX = (int) (area.getCenterX() + 0.5);
        final int centerY = (int) (area.getCenterY() + 0.5);
        final int rad = (int) (area.getWidth() * 0.5);

        final int baseHeight = house.getBaseHeight();
        final HeightMap hmT = ti.getHeightMap();

        // clear area above floor level
        Pen pen = new Pen() {

            @Override
            public void draw(int x, int z) {
                int terrain = hmT.apply(x, z);
                
                // put foundation concrete below 
                for (int y = terrain + 1; y < baseHeight; y++) {
                    brush.setBlock(x, y, z, BlockTypes.BUILDING_FOUNDATION);
                }

                // lay floor level
                brush.setBlock(x, baseHeight, z, BlockTypes.BUILDING_FLOOR);

                // clear area above floor level
                for (int y = baseHeight + 1; y <= terrain; y++) {
                    brush.setBlock(x, y, z, BlockTypes.AIR);
                }
            }
            
        };
        
        brush.fillCircle(centerX, centerY, rad, pen);
        
        for (int y = 0; y < house.getWallHeight(); y++) {
            HeightMap hm = HeightMaps.constant(baseHeight + y + 1); 
            brush.drawCircle(centerX, centerY, rad, hm, BlockTypes.BUILDING_WALL);
        }

        // door
        SimpleDoor door = house.getDoor();
        brush.fillRect(door.getRect(), door.getBaseHeight() + 1, door.getTopHeight() + 1, BlockTypes.AIR);

        RasterRegistry registry = StandardRegistry.getInstance();

        // windows
        for (Window wnd : house.getWindows()) {
            registry.rasterize(brush, ti, wnd);
        }
        
        registry.rasterize(brush, ti, house.getRoof());
    }

}
