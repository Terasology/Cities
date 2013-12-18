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

import org.terasology.cities.BlockTypes;
import org.terasology.cities.model.Building;
import org.terasology.cities.model.SimpleLot;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.RasterRegistry;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.raster.TerrainInfo;

/**
 * Draws the lot layout and all contained buildings
 * @author Martin Steiger
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
