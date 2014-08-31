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

import static org.terasology.commonworld.Orientation.EAST;
import static org.terasology.commonworld.Orientation.WEST;

import java.awt.Rectangle;

import org.terasology.cities.BlockTypes;
import org.terasology.cities.model.roof.SaddleRoof;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.raster.TerrainInfo;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.commonworld.heightmap.HeightMapAdapter;
import org.terasology.commonworld.heightmap.HeightMaps;

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

        final HeightMap bottomHm = HeightMaps.offset(topHm, -1);
        
        brush.fillRect(area, bottomHm, topHm, BlockTypes.ROOF_SADDLE);
        
        HeightMap gableBottomHm = new HeightMapAdapter() {

            @Override
            public int apply(int x, int z) {
                int h0 = roof.getBaseHeight();
                if (alongX) {
                    int left = area.x + 1;                     // building border is +1
                    int right = area.x + area.width - 1 - 1;   // building border is -1
                    
                    if (x == left || x == right) {
                        return h0;
                    }
                } else {
                    int top = area.y + 1;                      // building border is +1
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
