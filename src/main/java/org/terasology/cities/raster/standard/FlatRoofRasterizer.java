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
import org.terasology.cities.model.FlatRoof;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.raster.TerrainInfo;
import org.terasology.cities.terrain.ConstantHeightMap;
import org.terasology.cities.terrain.HeightMap;
import org.terasology.cities.terrain.HeightMapAdapter;

/**
 * Converts a {@link FlatRoof} into blocks
 * @author Martin Steiger
 */
public class FlatRoofRasterizer implements Rasterizer<FlatRoof> {
    
    @Override
    public void raster(Brush brush, TerrainInfo ti, final FlatRoof roof) {
        final Rectangle area = roof.getArea();

        if (!brush.affects(area)) {
            return;
        }
        
        HeightMap topHm = new HeightMapAdapter() {

            @Override
            public int apply(int x, int z) {
                int rx = x - area.x;
                int rz = z - area.y;

                int y = roof.getBaseHeight() + 1;       // at least one block thick

                // distance to border of the roof
                int borderDistX = Math.min(rx, area.width - 1 - rx);
                int borderDistZ = Math.min(rz, area.height - 1 - rz);

                int dist = Math.min(borderDistX, borderDistZ);
                
                if (dist == 0) {
                    y += roof.getBorderHeight();
                }
                
                return y;
            }
        };

        HeightMap bottomHm = new ConstantHeightMap(roof.getBaseHeight());
        
        brush.fillRect(area, bottomHm, topHm, BlockTypes.ROOF_FLAT);
    }

}
