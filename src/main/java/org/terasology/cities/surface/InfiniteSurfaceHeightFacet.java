// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.cities.surface;

import org.terasology.engine.world.generation.WorldFacet;
import org.terasology.math.geom.BaseVector2i;

/**
 *
 */
public interface InfiniteSurfaceHeightFacet extends WorldFacet {

    default float getWorld(BaseVector2i worldPos) {
        return getWorld(worldPos.getX(), worldPos.getY());
    }

    float getWorld(int worldX, int worldY);
}
