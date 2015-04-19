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

import java.awt.Rectangle;

/**
 * A hip roof
 */
public class HipRoof extends RectangularRoof {

    private final int maxHeight;
    private final double pitch;
    
    /**
     * @param rc the roof area
     * @param baseHeight the base height of the roof
     * @param maxHeight the maximum height of the roof
     * @param pitch the roof pitch
     */
    public HipRoof(Rectangle rc, int baseHeight, double pitch, int maxHeight) {
        super(rc, baseHeight);
        
        this.maxHeight = maxHeight;
        this.pitch = pitch;
    }

    /**
     * @param rc the roof area
     * @param baseHeight the base height of the roof
     * @param pitch the roof pitch
     */
    public HipRoof(Rectangle rc, int baseHeight, double pitch) {
        this(rc, baseHeight, pitch, Integer.MAX_VALUE);
    }

    /**
     * @return the maximum height of the roof
     */
    public int getMaxHeight() {
        return this.maxHeight;
    }

    /**
     * @return the slope
     */
    public double getPitch() {
        return this.pitch;
    }

}
