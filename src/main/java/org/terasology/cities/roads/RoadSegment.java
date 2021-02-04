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

package org.terasology.cities.roads;

import org.joml.Vector2i;
import org.joml.Vector2ic;

/**
 * A road segment is a part of a road with direction. One segment's end is the next segment's start.
 */
public class RoadSegment {

    private final Vector2i start = new Vector2i();
    private final Vector2i end = new Vector2i();
    private final float width;

    public RoadSegment(Vector2ic start, Vector2ic end, float width) {
        this.start.set(start);
        this.end.set(end);
        this.width = width;
    }

    /**
     * @return the start point
     */
    public Vector2ic getStart() {
        return start;
    }

    /**
     * @return the end point
     */
    public Vector2ic getEnd() {
        return end;
    }

    /**
     * @return the length of the road segment in blocks
     */
    public float getLength() {
        return (float) start.distance(end);
    }

    /**
     * @return the width of the road in blocks
     */
    public float getWidth() {
        return width;
    }
}
