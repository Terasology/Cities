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

package org.terasology.cities.walls;

import org.terasology.math.geom.BaseVector2i;

/**
 * A straight wall segment with a gate
 */
public class GateWallSegment extends WallSegment {

    private int wallHeight;

    /**
     * @param start one end of the wall
     * @param end the other end of the wall
     * @param wallThickness the wall thickness in block
     * @param wallHeight the wall height in blocks above terrain
     */
    public GateWallSegment(BaseVector2i start, BaseVector2i end, int wallThickness, int wallHeight) {
        super(start, end, wallThickness);
        this.wallHeight = wallHeight;
    }

    /**
     * @return the wall height in blocks above terrain
     */
    public int getWallHeight() {
        return this.wallHeight;
    }

    @Override
    public boolean isGate() {
        return true;
    }
}
