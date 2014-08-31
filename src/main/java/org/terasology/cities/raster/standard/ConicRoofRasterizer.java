/*
 * Copyright 2014 MovingBlocks
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

import java.awt.geom.Ellipse2D;

import org.terasology.cities.BlockTypes;
import org.terasology.cities.model.roof.ConicRoof;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.raster.TerrainInfo;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.commonworld.heightmap.HeightMapAdapter;
import org.terasology.math.TeraMath;

/**
 * Converts a {@link ConicRoof} into blocks
 * @author Martin Steiger
 */
public class ConicRoofRasterizer implements Rasterizer<ConicRoof> {
    
    @Override
    public void raster(Brush brush, TerrainInfo ti, final ConicRoof roof) {
        final Ellipse2D area = roof.getArea();

        if (!brush.affects(area)) {
            return;
        }
        
        final int centerX = (int) (area.getCenterX() + 0.5);
        final int centerY = (int) (area.getCenterY() + 0.5);
        final int rad = (int) (area.getWidth() * 0.5);
        
        HeightMap hm = new HeightMapAdapter() {

            @Override
            public int apply(int x, int z) {
                int rx = x - centerX;
                int rz = z - centerY;

                // relative distance to border of the roof
                double dist = rad - Math.sqrt(rx * rx + rz * rz);

                int y = TeraMath.floorToInt(roof.getBaseHeight() + dist * roof.getPitch());
                return y;
            }
        };

        brush.fillCircle(centerX, centerY, rad, hm, BlockTypes.ROOF_HIP);
    }

}
