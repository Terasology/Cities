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
import java.awt.Shape;

import org.terasology.cities.BlockTypes;
import org.terasology.cities.model.HipRoof;
import org.terasology.cities.model.Roof;
import org.terasology.cities.model.SaddleRoof;
import org.terasology.cities.model.SimpleChurch;
import org.terasology.cities.model.SimpleDoor;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.RasterRegistry;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.raster.TerrainInfo;
import org.terasology.cities.terrain.HeightMap;
import org.terasology.cities.terrain.OffsetHeightMap;

/**
 * Converts a {@link SimpleChurch} into blocks
 * @author Martin Steiger
 */
public class SimpleChurchRasterizer implements Rasterizer<SimpleChurch> {

    @Override
    public void raster(Brush brush, TerrainInfo ti, SimpleChurch blg) {
        Shape shape = blg.getLayout();
        
        RasterRegistry registry = StandardRegistry.getInstance();

        if (brush.affects(shape)) {
            prepareFloor(brush, blg.getNaveRect(), ti.getHeightMap(), blg.getBaseHeight(), BlockTypes.BUILDING_FLOOR);
            prepareFloor(brush, blg.getTowerRect(), ti.getHeightMap(), blg.getBaseHeight(), BlockTypes.BUILDING_FLOOR);
            
            brush.frame(blg.getNaveRect(), blg.getBaseHeight(), blg.getHallHeight(), BlockTypes.BUILDING_WALL);
            brush.frame(blg.getTowerRect(), blg.getBaseHeight(), blg.getTowerHeight(), BlockTypes.BUILDING_WALL);
            
            // door
            SimpleDoor door = blg.getDoor();
            brush.fillRect(door.getRect(), door.getBaseHeight(), door.getTopHeight(), BlockTypes.AIR);
        }
        
        registry.rasterize(brush, ti, blg.getNaveRoof()); 
        registry.rasterize(brush, ti, blg.getTowerRoof()); 
    }

    private void prepareFloor(Brush brush, Rectangle rc, HeightMap terrain, int baseHeight, BlockTypes floor) {
        
        // clear area above floor level
        brush.fillRect(rc, baseHeight, new OffsetHeightMap(terrain, 1), BlockTypes.AIR);

        // lay floor level
        brush.fillRect(rc, baseHeight - 1, baseHeight, floor);

        // put foundation concrete below 
        brush.fillRect(rc, terrain, baseHeight - 1, BlockTypes.BUILDING_FOUNDATION);
        
    }

}
