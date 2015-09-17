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

package org.terasology.cities.raster;

import java.util.Set;

import org.terasology.cities.BlockTypes;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.commonworld.heightmap.HeightMaps;
import org.terasology.math.Region3i;
import org.terasology.math.Side;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.LineSegment;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2f;

/**
 * Converts model elements into blocks
 */
public interface RasterTarget {

    /**
     * @param x x in world coords
     * @param y y in world coords
     * @param z z in world coords
     * @param type the block type
     */
    void setBlock(int x, int y, int z, BlockTypes type);

    /**
     * @param x x in world coords
     * @param y y in world coords
     * @param z z in world coords
     * @param type the block type
     * @param side the side (used to find the right block from the family)
     */
    void setBlock(int x, int y, int z, BlockTypes type, Set<Side> side);

    /**
     * @return the maximum drawing height
     */
    int getMaxHeight();

    /**
     * @return the maximum drawing height
     */
    int getMinHeight();

    /**
     * @return the area that is drawn by this raster target
     */
    Rect2i getAffectedArea();

    /**
     * @param rect the rectangle that should be drawn
     * @return the intersection between the brush area and the rectangle
     */
    default Rect2i getIntersectionArea(Rect2i rect) {
        return getAffectedArea().intersect(rect);
    }
}
