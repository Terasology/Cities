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
import org.terasology.cities.model.SaddleRoof;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.raster.TerrainInfo;
import org.terasology.cities.terrain.HeightMap;
import org.terasology.cities.terrain.HeightMapAdapter;
import org.terasology.cities.terrain.OffsetHeightMap;
import static org.terasology.cities.common.Orientation.*;

/**
 * Converts a {@link SaddleRoof} into blocks
 * @author Martin Steiger
 */
public class SaddleRoofRasterizer implements Rasterizer<SaddleRoof> {
    
    @Override
    public void raster(Brush brush, TerrainInfo ti, final SaddleRoof roof) {
        final Rectangle area = roof.getArea();

        if (!brush.affects(area)) {
            return;
        }
        
        final boolean alongX = roof.getOrientation() == EAST || roof.getOrientation() == WEST;
        
        final HeightMap topHm = new HeightMapAdapter() {

            @Override
            public int apply(int x, int z) {
                int rx = x - area.x;
                int rz = z - area.y;

                int y = roof.getBaseHeight();

                // distance to border of the roof
                int borderDistX = Math.min(rx, area.width - 1 - rx);
                int borderDistZ = Math.min(rz, area.height - 1 - rz);

                if (alongX) {
                    y += borderDistZ / roof.getPitch();
                } else {
                    y += borderDistX / roof.getPitch();
                }
                
                return y;
            }
        };

        final HeightMap bottomHm = new OffsetHeightMap(topHm, -1);
        
        brush.fillRect(area, bottomHm, topHm, BlockTypes.ROOF_SADDLE);
        
        HeightMap gableBottomHm = new HeightMapAdapter() {

            @Override
            public int apply(int x, int z) {
                int h0 = roof.getBaseHeight();
                if (alongX) {
                    int left = area.x + 1;                   // building border is +1
                    int right = left + area.width - 1 - 1;   // building border is -1
                    
                    if (x == left || x == right) {
                        return h0;
                    }
                } else {
                    int top = area.y + 1;                   // building border is +1
                    int bottom = area.y + area.height - 1 - 1; // building border is -1
                    if (z == top || z == bottom) {
                        return h0;
                    }
                }
                
                return bottomHm.apply(x, z);        // return top-height to get a no-op
            }
        };
        
        brush.fillRect(area, gableBottomHm, bottomHm, BlockTypes.ROOF_GABLE);
    }
}
