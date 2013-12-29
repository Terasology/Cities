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
import org.terasology.cities.model.HipRoof;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.raster.TerrainInfo;
import org.terasology.cities.terrain.HeightMap;
import org.terasology.cities.terrain.HeightMapAdapter;
import org.terasology.cities.terrain.OffsetHeightMap;
import org.terasology.math.TeraMath;

/**
 * Converts a {@link HipRoof} into blocks
 * @author Martin Steiger
 */
public class HipRoofRasterizer implements Rasterizer<HipRoof> {
    
    @Override
    public void raster(Brush brush, TerrainInfo ti, final HipRoof roof) {
        final Rectangle area = roof.getArea();

        if (!brush.affects(area)) {
            return;
        }
        
        // this is the ground truth
        // maxHeight = baseHeight + Math.min(cur.width, cur.height) * pitch / 2;
        
        HeightMap hm = new HeightMapAdapter() {

            @Override
            public int apply(int x, int z) {
                int rx = x - area.x;
                int rz = z - area.y;

                // distance to border of the roof
                int borderDistX = Math.min(rx, area.width - 1 - rx);
                int borderDistZ = Math.min(rz, area.height - 1 - rz);

                int dist = Math.min(borderDistX, borderDistZ);

                int y = TeraMath.floorToInt(roof.getBaseHeight() + dist * roof.getPitch());
                return Math.min(y, roof.getMaxHeight());
            }
        };

        brush.fillRect(area, hm, new OffsetHeightMap(hm, (int) roof.getPitch()), BlockTypes.ROOF_HIP);
    }

}
