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

package org.terasology.cities.model.roof;

import org.terasology.commonworld.Orientation;
import org.terasology.math.geom.Rect2i;

import com.google.common.base.Preconditions;

/**
 * A roof consisting of a single sloping surface
 */
public class PentRoof extends RectangularRoof {

    private final double pitch;
    private final Orientation orientation;

    /**
     * @param rc the roof shape
     * @param baseHeight the base height of the roof
     * @param pitch the roof pitch
     * @param orientation where the top edge is
     */
    public PentRoof(Rect2i rc, int baseHeight, Orientation orientation, double pitch) {
        super(rc, baseHeight);

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
