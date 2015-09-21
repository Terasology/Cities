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

package org.terasology.cities.roof;

import static org.terasology.commonworld.Orientation.EAST;
import static org.terasology.commonworld.Orientation.WEST;

import org.terasology.cities.BlockTheme;
import org.terasology.cities.BlockTypes;
import org.terasology.cities.model.roof.SaddleRoof;
import org.terasology.cities.raster.Pen;
import org.terasology.cities.raster.Pens;
import org.terasology.cities.raster.RasterTarget;
import org.terasology.cities.raster.RasterUtil;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.commonworld.heightmap.HeightMaps;
import org.terasology.math.geom.Rect2i;

/**
 * Converts a {@link SaddleRoof} into blocks
 */
public class SaddleRoofRasterizer extends RoofRasterizer<SaddleRoof> {

    /**
     * @param theme the block theme to use
     */
    public SaddleRoofRasterizer(BlockTheme theme) {
        super(theme, SaddleRoof.class);
    }

    @Override
    public void raster(RasterTarget target, SaddleRoof roof, HeightMap hm) {
        Rect2i area = roof.getArea();

        if (!area.overlaps(target.getAffectedArea())) {
            return;
        }

        final boolean alongX = roof.getOrientation() == EAST || roof.getOrientation() == WEST;

        HeightMap hmTop = new HeightMap() {

            @Override
            public int apply(int x, int z) {
                int rx = x - area.minX();
                int rz = z - area.minY();

                int y = roof.getBaseHeight();

                // distance to border of the roof
                int borderDistX = Math.min(rx, area.width() - 1 - rx);
                int borderDistZ = Math.min(rz, area.height() - 1 - rz);

                if (alongX) {
                    y += borderDistZ / roof.getPitch();
                } else {
                    y += borderDistX / roof.getPitch();
                }

                return y;
            }
        };

        HeightMap hmBottom = HeightMaps.offset(hmTop, -1);
        Pen pen = Pens.fill(target, hmBottom, hmTop, BlockTypes.ROOF_SADDLE);
        RasterUtil.fillRect(pen, area);

        HeightMap hmGableBottom = new HeightMap() {

            @Override
            public int apply(int x, int z) {
                int h0 = roof.getBaseHeight();
                if (alongX) {
                    int left = area.minX() + 1;    // building border is +1
                    int right = area.maxX() - 1;   // building border is -1

                    if (x == left || x == right) {
                        return h0;
                    }
                } else {
                    int top = area.minY() + 1;    // building border is +1
                    int bottom = area.maxY() - 1;  // building border is -1
                    if (z == top || z == bottom) {
                        return h0;
                    }
                }

                return hmBottom.apply(x, z);        // return top-height to get a no-op
            }
        };

        pen = Pens.fill(target, hmGableBottom, hmBottom, BlockTypes.ROOF_GABLE);
        RasterUtil.fillRect(pen, area);
    }
}
