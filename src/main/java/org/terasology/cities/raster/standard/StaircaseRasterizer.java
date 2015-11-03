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

import java.util.Iterator;

import org.terasology.cities.BlockTheme;
import org.terasology.cities.BlockTypes;
import org.terasology.cities.bldg.BuildingPartRasterizer;
import org.terasology.cities.bldg.RectBuildingPart;
import org.terasology.cities.bldg.StaircaseBuildingPart;
import org.terasology.cities.common.Edges;
import org.terasology.cities.raster.BuildingPens;
import org.terasology.cities.raster.Pen;
import org.terasology.cities.raster.Pens;
import org.terasology.cities.raster.RasterTarget;
import org.terasology.cities.raster.RasterUtil;
import org.terasology.commonworld.Orientation;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.math.Side;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.RectIterable;
import org.terasology.math.geom.Vector2i;

/**
 * Converts a {@link RectBuildingPart} into blocks
 */
public class StaircaseRasterizer extends BuildingPartRasterizer<StaircaseBuildingPart> {

    public StaircaseRasterizer(BlockTheme theme) {
        super(theme, StaircaseBuildingPart.class);
    }

    @Override
    protected void raster(RasterTarget target, StaircaseBuildingPart part, HeightMap heightMap) {
        Rect2i rc = part.getShape();

        if (!rc.overlaps(target.getAffectedArea())) {
            return;
        }

        Rect2i stairsRect = rc.expand(-1, -1);
        Orientation o = part.getOrientation();
        Vector2i entry = Edges.getCorner(stairsRect, o);
        Iterator<BaseVector2i> it = new RectIterable(stairsRect, false, entry).iterator();
        it.next(); // don't start right in front of the door
        it.next(); // don't start right in front of the door
        int y = part.getBaseHeight();
        while (y < part.getTopHeight() && y <= target.getMaxHeight()) {
            BaseVector2i v = it.next();
            // parts of the staircase could be outside
            if (target.getAffectedArea().contains(v.getX(), v.getY())) {
                target.setBlock(v.getX(), y, v.getY(), BlockTypes.TOWER_STAIRS);
            }
            y++;
        }
    }
}

