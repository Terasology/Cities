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

package org.terasology.cities.blocked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.terasology.math.Region3i;
import org.terasology.math.geom.ImmutableVector2i;
import org.terasology.math.geom.Rect2i;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.facets.base.BaseFacet2D;

/**
 * An image-based registry for blocked areas.
 */
public class BlockedAreaFacet extends BaseFacet2D {

    private final Collection<BlockedArea> areas = new ArrayList<>();

    public BlockedAreaFacet(Region3i targetRegion, Border3D border) {
        super(targetRegion, border);
    }

    public void add(BlockedArea area) {
        areas.add(area);
    }

    Collection<BlockedArea> getAreas() {
        return Collections.unmodifiableCollection(areas);
    }

    /**
     * @param wx the world x coordinate
     * @param wy the world y coordinate
     * @return true if blocked, false is not or unknown
     */
    public boolean isBlocked(int wx, int wy) {
        for (BlockedArea area : areas) {
            if (area.getWorldRegion().contains(wx, wy)) {
                return area.isBlocked(wx, wy);
            }
        }
        return false;
    }


    public boolean isBlocked(Rect2i shape) {
        for (BlockedArea area : areas) {
            if (area.getWorldRegion().overlaps(shape)) {
                if (area.isBlocked(shape)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addLine(ImmutableVector2i start, ImmutableVector2i end, float width) {
        // TODO: maybe check for line/rect intersection with this FACET first
        for (BlockedArea area : areas) {
            area.addLine(start, end, width);
        }
    }

    public void addRect(Rect2i rc) {
        for (BlockedArea area : areas) {
            area.addRect(rc);
        }
    }

}
