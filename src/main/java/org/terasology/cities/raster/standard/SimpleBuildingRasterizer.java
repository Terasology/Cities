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
import org.terasology.cities.model.SimpleBuilding;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.RasterRegistry;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.math.Vector3i;

/**
 * Converts a {@link SimpleBuilding} into blocks
 * @author Martin Steiger
 */
public class SimpleBuildingRasterizer implements Rasterizer<SimpleBuilding> {

    @Override
    public void raster(Brush brush, SimpleBuilding blg) {
        Rectangle rc = blg.getLayout();
        
        if (brush.affects(rc)) {
        
            int baseHeight = blg.getBaseHeight();
            int wallHeight = blg.getWallHeight();
    
            brush.clearAbove(rc, baseHeight);
            
            brush.fill(rc, baseHeight - 1, baseHeight, BlockTypes.BUILDING_FLOOR);
            brush.fillAirBelow(rc, baseHeight - 2, BlockTypes.BUILDING_FLOOR);
            
            // wall along z
            brush.createWallZ(rc.y, rc.y + rc.height, rc.x, baseHeight, wallHeight, BlockTypes.BUILDING_WALL);
            brush.createWallZ(rc.y, rc.y + rc.height, rc.x + rc.width - 1, baseHeight, wallHeight, BlockTypes.BUILDING_WALL);
    
            // wall along x
            brush.createWallX(rc.x, rc.x + rc.width, rc.y, baseHeight, wallHeight, BlockTypes.BUILDING_WALL);
            brush.createWallX(rc.x, rc.x + rc.width, rc.y + rc.height - 1, baseHeight, wallHeight, BlockTypes.BUILDING_WALL);
    
            // door
            Rectangle door = blg.getDoor();
            Vector3i doorFrom = new Vector3i(door.x, baseHeight, door.y);
            Vector3i doorTo = new Vector3i(door.x + door.width, baseHeight + blg.getDoorHeight(), door.y + door.height);
            brush.fill(doorFrom, doorTo, BlockTypes.AIR);
        }
        
        // the roof can be larger than the building -- not sure if this is a good idea ...
        
        RasterRegistry registry = StandardRegistry.getInstance();
        registry.rasterize(brush, blg.getRoof());
    }

}
