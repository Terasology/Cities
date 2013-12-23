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
import org.terasology.cities.model.GateWallSegment;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.raster.TerrainInfo;
import org.terasology.cities.terrain.HeightMap;
import org.terasology.cities.terrain.HeightMapAdapter;
import org.terasology.cities.terrain.OffsetHeightMap;

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
        
        HeightMap topHm = new OffsetHeightMap(terrain, height);

        
        brush.draw(bottomHm, topHm, x1, z1, x2, z2, BlockTypes.TOWER_WALL);
    }

}
