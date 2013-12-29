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
import org.terasology.cities.common.Rectangles;
import org.terasology.cities.model.PentRoof;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.raster.TerrainInfo;
import org.terasology.cities.terrain.HeightMap;
import org.terasology.cities.terrain.HeightMapAdapter;
import org.terasology.cities.terrain.OffsetHeightMap;
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
        brush.fillRect(area, bottomHm, new OffsetHeightMap(bottomHm, thickness), BlockTypes.ROOF_HIP);
        
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
