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

import java.awt.Rectangle;

import org.terasology.cities.BlockTypes;
import org.terasology.cities.model.roof.FlatRoof;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.raster.TerrainInfo;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.commonworld.heightmap.HeightMapAdapter;
import org.terasology.commonworld.heightmap.HeightMaps;

/**
 * Converts a {@link FlatRoof} into blocks
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
                    y += roof.getBorderHeight(rx, rz);
                }
                
                return y;
            }
        };

        HeightMap bottomHm = HeightMaps.constant(roof.getBaseHeight());
        
        brush.fillRect(area, bottomHm, topHm, BlockTypes.ROOF_FLAT);
    }

}
