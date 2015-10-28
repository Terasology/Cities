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

import org.terasology.cities.BlockTheme;
import org.terasology.cities.BlockTypes;
import org.terasology.cities.model.roof.PentRoof;
import org.terasology.cities.raster.Pen;
import org.terasology.cities.raster.Pens;
import org.terasology.cities.raster.RasterTarget;
import org.terasology.cities.raster.RasterUtil;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.commonworld.heightmap.HeightMaps;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Rect2i;

/**
 * Converts a {@link PentRoof} into blocks
 */
public class PentRoofRasterizer extends RoofRasterizer<PentRoof> {

    /**
     * @param theme the block theme to use
     */
    public PentRoofRasterizer(BlockTheme theme) {
        super(theme, PentRoof.class);
    }

    @Override
    public void raster(RasterTarget target, PentRoof roof, HeightMap hm) {
        Rect2i area = roof.getArea();

        if (!area.overlaps(target.getAffectedArea())) {
            return;
        }

        final HeightMap hmBottom = new HeightMap() {

            @Override
            public int apply(int x, int z) {
                int rx = x - area.minX();
                int rz = z - area.minY();

                BaseVector2i dir = roof.getOrientation().getDir();

                if (dir.getX() < 0) {
                    rx -= area.width();
                }

                if (dir.getY() < 0) {
                    rz -= area.height();
                }

                int hx = rx * dir.getX();
                int hz = rz * dir.getY();

                int h = TeraMath.floorToInt(Math.max(hx, hz) * roof.getPitch());

                return roof.getBaseHeight() + h;
            }
        };

        int thickness = TeraMath.ceilToInt(roof.getPitch());
        HeightMap hmTop = HeightMaps.offset(hmBottom, thickness);
        Pen pen = Pens.fill(target, hmBottom, hmTop, BlockTypes.ROOF_HIP);
        RasterUtil.fillRect(pen, area);

        final Rect2i wallRect = roof.getArea().expand(-1, -1);

        HeightMap hmGableBottom = new HeightMap() {

            @Override
            public int apply(int x, int z) {
                int h0 = roof.getBaseHeight();

                boolean onZ = (x == wallRect.minX() || x == wallRect.maxX());
                boolean zOk = (z >= wallRect.minY() && z <= wallRect.maxY());

                if (onZ && zOk) {
                    return h0;
                }

                boolean onX = (z == wallRect.minY() || z == wallRect.maxY());
                boolean xOk = (x >= wallRect.minX() && x <= wallRect.maxX());

                if (onX && xOk) {
                    return h0;
                }

                return hmBottom.apply(x, z); // return top-height to get a no-op
            }
        };

        pen = Pens.fill(target, hmGableBottom, hmBottom, BlockTypes.ROOF_GABLE);
        RasterUtil.fillRect(pen, area);
    }
}
