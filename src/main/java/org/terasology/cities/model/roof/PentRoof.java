// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.cities.model.roof;

import com.google.common.base.Preconditions;
import org.terasology.commonworld.Orientation;
import org.terasology.math.geom.Rect2i;

/**
 * A roof consisting of a single sloping surface
 */
public class PentRoof extends RectangularRoof {

    private final double pitch;
    private final Orientation orientation;

    /**
     * @param baseRect the building rectangle (must be fully inside <code>withEaves</code>).
     * @param withEaves the roof area including eaves (=overhang)
     * @param baseHeight the base height of the roof
     * @param pitch the roof pitch
     * @param orientation where the top edge is
     */
    public PentRoof(Rect2i baseRect, Rect2i withEaves, int baseHeight, Orientation orientation, double pitch) {
        super(baseRect, withEaves, baseHeight);

        Preconditions.checkArgument(pitch > 0 && pitch < 10, "pitch must be in [0..10]");

        this.orientation = orientation;
        this.pitch = pitch;
    }

    /**
     * @return the pitch
     */
    public double getPitch() {
        return pitch;
    }

    /**
     * @return where the top edge is
     */
    public Orientation getOrientation() {
        return orientation;
    }

}
