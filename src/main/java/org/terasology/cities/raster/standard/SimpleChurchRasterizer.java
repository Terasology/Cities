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
import org.terasology.cities.model.bldg.BuildingPart;
import org.terasology.cities.model.bldg.SimpleBuildingPart;
import org.terasology.cities.model.bldg.SimpleChurch;
import org.terasology.cities.model.bldg.SimpleDoor;
import org.terasology.cities.model.bldg.Window;
import org.terasology.cities.model.roof.Roof;
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
