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

import org.terasology.cities.BlockTypes;
import org.terasology.cities.model.SimpleLot;
import org.terasology.cities.model.bldg.Building;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.RasterRegistry;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.raster.TerrainInfo;

/**
 * Draws the lot layout and all contained buildings
 */
public class SimpleLotRasterizer implements Rasterizer<SimpleLot> {

    @Override
    public void raster(Brush brush, TerrainInfo ti, SimpleLot lot) {

        if (!brush.affects(lot.getShape())) {
            return;
        }

        RasterRegistry registry = StandardRegistry.getInstance();
        
        brush.fillRect(lot.getShape(), ti.getHeightMap(), BlockTypes.LOT_EMPTY);
        
        // draw buildings
        for (Building blg : lot.getBuildings()) {
            
            registry.rasterize(brush, ti, blg);
        }

        // draw fence
        if (lot.getFence().isPresent()) {
            registry.rasterize(brush, ti, lot.getFence().get());
        }
        
        
    }
    
}
