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
import org.terasology.cities.model.BuildingPart;
import org.terasology.cities.model.Roof;
import org.terasology.cities.model.SimpleBuildingPart;
import org.terasology.cities.model.SimpleChurch;
import org.terasology.cities.model.SimpleDoor;
import org.terasology.cities.model.Window;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.RasterRegistry;
import org.terasology.cities.raster.TerrainInfo;

/**
 * Converts a {@link SimpleChurch} into blocks
 * @author Martin Steiger
 */
public class SimpleChurchRasterizer extends AbstractRasterizer<SimpleChurch> {

    @Override
    public void raster(Brush brush, TerrainInfo ti, SimpleChurch blg) {
        
        for (BuildingPart bp : blg.getParts()) {
            rasterBuildingPart(brush, ti, (SimpleBuildingPart)bp);
            rasterBuildingPart(brush, ti, (SimpleBuildingPart)bp);
        }
        
        RasterRegistry registry = StandardRegistry.getInstance();

        // windows
        for (Window wnd : blg.getWindows()) {
            registry.rasterize(brush, ti, wnd);
        }
        
        // door
        SimpleDoor door = blg.getDoor();
        brush.fillRect(door.getRect(), door.getBaseHeight(), door.getTopHeight(), BlockTypes.AIR);
    }

    private void rasterBuildingPart(Brush brush, TerrainInfo ti, SimpleBuildingPart part) {
        Rectangle shape = part.getLayout();
        
        if (brush.affects(shape)) {
            prepareFloor(brush, shape, ti.getHeightMap(), part.getBaseHeight(), BlockTypes.BUILDING_FLOOR);
            brush.frame(shape, part.getBaseHeight(), part.getTopHeight(), BlockTypes.BUILDING_WALL);
        }
        
        Roof roof = part.getRoof();
        
        if (brush.affects(roof.getArea())) {
            RasterRegistry registry = StandardRegistry.getInstance();

            registry.rasterize(brush, ti, roof);
        }
    }
}
