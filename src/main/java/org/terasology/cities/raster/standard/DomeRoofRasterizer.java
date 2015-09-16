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
import org.terasology.cities.model.roof.DomeRoof;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.raster.TerrainInfo;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.math.geom.Rect2i;

/**
 * Converts a {@link DomeRoof} into blocks
 */
public class DomeRoofRasterizer implements Rasterizer<DomeRoof> {

    @Override
    public void raster(final Brush brush, TerrainInfo ti, final DomeRoof roof) {
        Rect2i area = roof.getArea();

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

        HeightMap topHm = new HeightMap() {

            @Override
            public int apply(int rx, int rz) {
                int height = roof.getHeight();
                double y = getY(area, height, rx + 0.5, rz + 0.5);        // measure at block center

                return roof.getBaseHeight() + (int) Math.max(1, y);
            }
        };

        HeightMap bottomHm = new HeightMap() {

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
