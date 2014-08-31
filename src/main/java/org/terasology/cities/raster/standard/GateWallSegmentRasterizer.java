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

import org.terasology.cities.BlockTypes;
import org.terasology.cities.model.bldg.GateWallSegment;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.raster.TerrainInfo;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.commonworld.heightmap.HeightMapAdapter;
import org.terasology.commonworld.heightmap.HeightMaps;

/**
 * Converts a {@link GateWallSegment} into blocks
 * @author Martin Steiger
 */
public class GateWallSegmentRasterizer implements Rasterizer<GateWallSegment> {

    @Override
    public void raster(Brush brush, TerrainInfo ti, GateWallSegment element) {
        
        final HeightMap terrain = ti.getHeightMap();
        
        final int x1 = element.getStart().x;
        final int z1 = element.getStart().y;
        final int x2 = element.getEnd().x;
        final int z2 = element.getEnd().y;
        final double width = Math.sqrt((x2 - x1) * (x2 - x1) + (z2 - z1) * (z2 - z1));
        final int height = element.getWallHeight();
        
        HeightMap bottomHm = new HeightMapAdapter() {
            
            @Override
            public int apply(int x, int z) {
                double cx = (x1 + x2) * 0.5;
                double cz = (z1 + z2) * 0.5;
                double dx = x - cx;
                double dz = z - cz;
 
                // dist is at maximum 0.5 * width
                double dist = Math.sqrt((dx * dx) + (dz * dz));
                
                // normalize
                dist = 2 * dist / width;
                
                // make it a squeezed circle shape
                dist = Math.min(1.0, dist * dist * 4);
                
                int base = terrain.apply(x, z);
                return (int) (base + height * (1.0 - dist) - 1);
            }
        };
        
        HeightMap topHm = HeightMaps.offset(terrain, height);

        
        brush.draw(bottomHm, topHm, x1, z1, x2, z2, BlockTypes.TOWER_WALL);
    }

}
