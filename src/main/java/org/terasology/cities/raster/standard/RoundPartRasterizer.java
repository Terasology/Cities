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

import java.math.RoundingMode;

import org.terasology.cities.BlockTheme;
import org.terasology.cities.BlockTypes;
import org.terasology.cities.bldg.BuildingPartRasterizer;
import org.terasology.cities.bldg.RoundBuildingPart;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.Pen;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.commonworld.heightmap.HeightMaps;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Circle;
import org.terasology.math.geom.Vector2i;

/**
 * Converts a {@link RoundBuildingPart} into blocks
 */
public class RoundPartRasterizer extends BuildingPartRasterizer<RoundBuildingPart> {

    /**
     * @param theme the theme to use
     */
    public RoundPartRasterizer(BlockTheme theme) {
        super(theme, RoundBuildingPart.class);
    }

    @Override
    protected void raster(Brush brush, RoundBuildingPart element, HeightMap heightMap) {

        Circle area = element.getShape();

        if (!area.intersects(brush.getAffectedArea())) {
            return;
        }

        Vector2i center = new Vector2i(area.getCenter(), RoundingMode.HALF_UP);

        final int baseHeight = element.getBaseHeight();

        // clear area above and below floor level
        Pen pen = new Pen() {

            @Override
            public void draw(int x, int z) {
                int terrain = heightMap.apply(x, z);

                // put foundation concrete below (including the top soil layer)
                for (int y = terrain; y < baseHeight; y++) {
                    brush.setBlock(x, y, z, BlockTypes.BUILDING_FOUNDATION);
                }

                // lay floor level
                brush.setBlock(x, baseHeight, z, BlockTypes.BUILDING_FLOOR);

                // clear area above floor level
                for (int y = baseHeight + 1; y <= terrain; y++) {
                    brush.setBlock(x, y, z, BlockTypes.AIR);
                }
            }
        };

        int radius = TeraMath.floorToInt(area.getRadius());
        brush.fillCircle(center.getX(), center.getY(), radius, pen);

        for (int y = 0; y < element.getWallHeight(); y++) {
            HeightMap hm = HeightMaps.constant(baseHeight + y + 1);
            brush.drawCircle(center.getX(), center.getY(), radius, hm, BlockTypes.BUILDING_WALL);
        }
    }
}
