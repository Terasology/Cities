// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.cities.walls;

import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.ImmutableVector2i;

/**
 * A straight wall segment
 */
public abstract class WallSegment {

    private final ImmutableVector2i start;
    private final ImmutableVector2i end;

    private final int wallThickness;

    /**
     * @param start one end of the wall
     * @param end the other end of the wall
     * @param wallThickness the wall thickness in block
     */
    public WallSegment(BaseVector2i start, BaseVector2i end, int wallThickness) {
        this.start = ImmutableVector2i.createOrUse(start);
        this.end = ImmutableVector2i.createOrUse(end);
        this.wallThickness = wallThickness;
    }

    /**
     * @return one end of the wall
     */
    public ImmutableVector2i getStart() {
        return this.start;
    }

    /**
     * @return the other end of the wall
     */
    public ImmutableVector2i getEnd() {
        return this.end;
    }

    /**
     * @return the wallThickness
     */
    public int getWallThickness() {
        return this.wallThickness;
    }

    /**
     * @return true if the segment is passable
     */
    public abstract boolean isGate();
}
