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

package org.terasology.cities.model.bldg;

import org.terasology.math.Vector2i;

/**
 * A straight wall segment
  */
public class WallSegment {

    private final Vector2i start;
    private final Vector2i end;
    
    private int wallThickness;

    /**
     * @param start one end of the wall
     * @param end the other end of the wall
     * @param wallThickness the wall thickness in block
     */
    public WallSegment(Vector2i start, Vector2i end, int wallThickness) {
        this.start = start;
        this.end = end;
        this.wallThickness = wallThickness;
    }

    /**
     * @return one end of the wall
     */
    public Vector2i getStart() {
        return this.start;
    }

    /**
     * @return the other end of the wall
     */
    public Vector2i getEnd() {
        return this.end;
    }

    /**
     * @return the wallThickness
     */
    public int getWallThickness() {
        return this.wallThickness;
    }

}
