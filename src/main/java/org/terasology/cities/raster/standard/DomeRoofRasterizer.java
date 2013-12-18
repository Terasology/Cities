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
import org.terasology.cities.raster.TerrainInfo;
import org.terasology.cities.terrain.HeightMap;
import org.terasology.cities.terrain.HeightMapAdapter;

/**
 * Converts a {@link DomeRoof} into blocks
 * @author Martin Steiger
 */
public class DomeRoofRasterizer implements Rasterizer<DomeRoof> {
    
    @Override
    public void raster(final Brush brush, TerrainInfo ti, final DomeRoof roof) {
        final Rectangle area = roof.getArea();

        if (!brush.affects(area)) {
            return;
        }

//        The surface of an ellipsoid is defined as the set of points(x, y, z) that ..
//        
//         x^2     y^2     z^2
//        ----- + ----- + ----- = 1
//         a^2     b^2     c^2
//        
//
//                 (      x^2     z^2  )
//       y^2 = b^2 ( 1 - ----- - ----- )
//                 (      a^2     c^2  )
//        
        
        HeightMap topHm = new HeightMapAdapter() {

            @Override
            public int apply(int rx, int rz) {
                int height = roof.getHeight();
                double y = getY(area, height, rx + 0.5, rz + 0.5);        // measure at block center
                
                return roof.getBaseHeight() + (int) Math.max(1, y);
            }
        };

        HeightMap bottomHm = new HeightMapAdapter() {

            @Override
            public int apply(int rx, int rz) {
                int baseHeight = roof.getBaseHeight();

                if (rx == area.x || rz == area.y) {
                    return baseHeight;
                }
                
                if (rx == area.x + area.getWidth() - 1 || rz == area.y + area.getHeight() - 1) {
                    return baseHeight;
                }
                
                int height = roof.getHeight();
                double y = getY(area, height, rx + 0.5, rz + 0.5);        // measure at block center
                
                return baseHeight + (int) Math.max(0, y - 2);
            }
        };
        
        brush.fillRect(area, bottomHm, topHm, BlockTypes.ROOF_FLAT);
    }

    private double getY(Rectangle area, int height, double rx, double rz) {
        
        double x = rx - area.x - area.width * 0.5;   // distance from the center
        double z = rz - area.y - area.height * 0.5;
        
        double a = area.width * 0.5;
        double b = height;
        double c = area.height * 0.5;
    
        double y2 = b * b * (1 - (x * x) / (a * a) - (z * z) / (c * c));
    
        return Math.sqrt(y2);
    }
}
