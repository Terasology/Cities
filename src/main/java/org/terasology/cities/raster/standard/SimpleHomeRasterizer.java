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

import org.terasology.cities.BlockTheme;
import org.terasology.cities.BlockTypes;
import org.terasology.cities.bldg.BuildingPartRasterizer;
import org.terasology.cities.bldg.RectBuildingPart;
import org.terasology.cities.raster.Brush;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.math.geom.Rect2i;

/**
 * Converts a {@link SimpleHome} into blocks
 */
public class SimpleHomeRasterizer extends BuildingPartRasterizer<RectBuildingPart> {

    public SimpleHomeRasterizer(BlockTheme theme) {
        super(theme, RectBuildingPart.class);
    }

    @Override
    protected void raster(Brush brush, RectBuildingPart part, HeightMap heightMap) {
        Rect2i rc = part.getShape();
//        int topHeight = part.getBaseHeight() + part.getWallHeight() + part.getRoof().getHeight;
//        Region3i bbox = Region3i(rc.minX(), part.getBaseHeight(), rc.minY(), rc.maxX(), topHeight, rc.maxY());

//        if (chunk.getRegion().overlaps(bbox)) {

        int baseHeight = part.getBaseHeight();
        int wallHeight = part.getWallHeight();

        prepareFloor(brush, rc, heightMap, baseHeight, BlockTypes.BUILDING_FLOOR);

        // create walls
        brush.frame(rc, baseHeight, part.getBaseHeight() + wallHeight, BlockTypes.BUILDING_WALL);
    }
}

