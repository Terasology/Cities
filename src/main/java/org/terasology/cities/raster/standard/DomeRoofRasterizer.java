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
import org.terasology.cities.model.DomeRoof;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.terrain.HeightMap;
import org.terasology.cities.terrain.HeightMapAdapter;
import org.terasology.cities.terrain.OffsetHeightMap;

/**
 * Converts a {@link DomeRoof} into blocks
 * @author Martin Steiger
 */
public class DomeRoofRasterizer implements Rasterizer<DomeRoof> {
    
    @Override
    public void raster(final Brush brush, final DomeRoof roof) {
        final Rectangle area = roof.getArea();

        if (!brush.affects(area)) {
            return;
        }

//        An ellipsoid is defined as
//        
//         x^2     y^2     z^2
//        ----- + ----- + ----- = 0
//         a^2     b^2     c^2
        
        HeightMap topHm = new HeightMapAdapter() {

            @Override
            public int apply(int rx, int rz) {
                double x = rx - area.x - area.width / 2;   // distance from the center
                double z = rz - area.y - area.height / 2;
                
                double a = area.width / 2;
                double b = roof.getHeight();
                double c = area.width / 2;

                for (int y = roof.getBaseHeight(); y < brush.getMaxHeight(); y++) {
                    double result = (x * x) / (a * a) + (y * y) / (b * b) + (z * z) / (c * c);
                    if (result > 1.0) {
                        return y;
                    }
                }
                
                return roof.getBaseHeight();
            }
        };

        HeightMap bottomHm = new OffsetHeightMap(topHm, -1);
        
        brush.fill(area, bottomHm, topHm, BlockTypes.ROOF_FLAT);
    }

}
