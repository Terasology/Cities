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

import java.awt.Rectangle;

import org.terasology.cities.BlockTypes;
import org.terasology.cities.common.Rectangles;
import org.terasology.cities.model.PentRoof;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.raster.TerrainInfo;
import org.terasology.cities.terrain.HeightMap;
import org.terasology.cities.terrain.HeightMapAdapter;
import org.terasology.cities.terrain.HeightMaps;
import org.terasology.math.TeraMath;
import org.terasology.math.Vector2i;

/**
 * Converts a {@link PentRoof} into blocks
 * @author Martin Steiger
 */
public class PentRoofRasterizer implements Rasterizer<PentRoof> {

    @Override
    public void raster(Brush brush, TerrainInfo ti, final PentRoof roof) {
        final Rectangle area = roof.getArea();

        if (!brush.affects(area)) {
            return;
        }
        
        final HeightMap bottomHm = new HeightMapAdapter() {

            @Override
            public int apply(int x, int z) {
                int rx = x - area.x;
                int rz = z - area.y;

                Vector2i dir = roof.getOrientation().getDir();
                
                if (dir.x < 0) {
                    rx -= area.width;
                }

                if (dir.y < 0) {
                    rz -= area.height;
                }

                int hx = rx * dir.x;
                int hz = rz * dir.y;
                
                int h = TeraMath.floorToInt(Math.max(hx, hz) * roof.getPitch());

                return roof.getBaseHeight() + h;
            }
        };

        int thickness = TeraMath.ceilToInt(roof.getPitch());
        brush.fillRect(area, bottomHm, HeightMaps.offset(bottomHm, thickness), BlockTypes.ROOF_HIP);
        
        final Rectangle wallRect = Rectangles.expandRect(roof.getArea(), -1);
        
        HeightMap gableBottomHm = new HeightMapAdapter() {

            @Override
            public int apply(int x, int z) {
                int h0 = roof.getBaseHeight();

                boolean onZ = (x == wallRect.x || x == wallRect.x + wallRect.width - 1);
                boolean zOk = (z >= wallRect.y && z <= wallRect.y + wallRect.height - 1);
                
                if (onZ && zOk) {
                    return h0;
                }

                boolean onX = (z == wallRect.y || z == wallRect.y + wallRect.height - 1);
                boolean xOk = (x >= wallRect.x && x <= wallRect.x + wallRect.width - 1);

                if (onX && xOk) {
                    return h0;
                }

                return bottomHm.apply(x, z); // return top-height to get a no-op
            }
        };
        
        brush.fillRect(area, gableBottomHm, bottomHm, BlockTypes.ROOF_GABLE);
    }
}
