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

package org.terasology.cities.terrain;

import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Rect2i;
import org.terasology.world.generation.WorldFacet;

/**
 *
 */
public interface BuildableTerrainFacet extends WorldFacet {

    default boolean isBuildable(BaseVector2i worldPos) {
        return isBuildable(worldPos.getX(), worldPos.getY());
    }

    boolean isBuildable(int worldX, int worldY);

    default boolean isBuildable(Rect2i rect) {
        // approximate by checking corners only
        // TODO: find a better solution
        return isBuildable(rect.minX(), rect.minY())
            && isBuildable(rect.minX(), rect.maxY())
            && isBuildable(rect.maxX(), rect.minY())
            && isBuildable(rect.maxX(), rect.maxY());
    }

    default boolean isPassable(BaseVector2i worldPos) {
        return isPassable(worldPos.getX(), worldPos.getY());
    }

    boolean isPassable(int worldX, int worldY);

    default boolean isPassable(Rect2i rect) {
        // approximate by checking corners only
        // TODO: find a better solution
        return isPassable(rect.minX(), rect.minY())
            && isPassable(rect.minX(), rect.maxY())
            && isPassable(rect.maxX(), rect.minY())
            && isPassable(rect.maxX(), rect.maxY());
    }
}
