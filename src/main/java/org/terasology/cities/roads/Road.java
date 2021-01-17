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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.joml.Vector2i;
import org.joml.Vector2ic;

import com.google.common.base.Preconditions;

/**
 * A road contains two end points and a list of points between them.
 */
public class Road {

    private final List<Vector2ic> segmentPoints;
    private final List<RoadSegment> segments;
    private final float width;
    private final float length;

    /**
     * The point list goes from start to end, but does not contain them
     * @param end0 one end point
     * @param end1 the other end point
     * @param width the width of the road
     */
    public Road(Vector2ic end0, Vector2ic end1, float width) {
        this(Arrays.asList(new Vector2i(end0), new Vector2i(end1)), width);
    }

    public Road(List<? extends Vector2ic> segPoints, float width) {
        Preconditions.checkArgument(segPoints.size() >= 2, "must contain at least two points");

        segmentPoints = new ArrayList<>(segPoints.size());

        float tmpLength = 0;
        Vector2ic prev = segPoints.get(0);
        for (Vector2ic segPoint : segPoints) {
            tmpLength += segPoint.distance(prev);
            prev = segPoint;
            segmentPoints.add(new Vector2i(segPoint));
        }

        segments = new ArrayList<>(segPoints.size() - 1);
        for (int i = 1; i < segPoints.size(); i++) {
            Vector2ic p = segmentPoints.get(i - 1);
            Vector2ic c = segmentPoints.get(i);
            segments.add(new RoadSegment(p, c, width));
        }

        this.length = tmpLength;
        this.width = width;
    }

    /**
     * @return the other end point
     */
    public Vector2ic getEnd1() {
        return segmentPoints.get(segmentPoints.size() - 1);
    }

    /**
     * @return one end point
     */
    public Vector2ic getEnd0() {
        return segmentPoints.get(0);
    }

    /**
     * @return an unmodifiable view on the segment points (at least two points)
     */
    public List<Vector2ic> getPoints() {
        return Collections.unmodifiableList(segmentPoints);
    }

    /**
     * @return an unmodifiable view on the segment points (at least one segment)
     */
    public List<RoadSegment> getSegments() {
        return Collections.unmodifiableList(segments);
    }

    /**
     * @param pos the coordinate to test
     * @return true, if the road ends at the given coordinate
     */
    public boolean endsAt(Vector2ic pos) {
        return getEnd0().equals(pos) || getEnd1().equals(pos);
    }

    /**
     * @param pos one end of the road
     * @return the other end
     * @throws IllegalArgumentException if not an end point of the road
     */
    public Vector2ic getOtherEnd(Vector2ic pos) {
        if (getEnd0().equals(pos)) {
            return getEnd1();
        }
        if (getEnd1().equals(pos)) {
            return getEnd0();
        }
        throw new IllegalArgumentException("not an end point of the road");
    }

    /**
     * @return the length of the road in blocks
     */
    public float getLength() {
        return length;
    }

    /**
     * @return the width of the road in blocks
     */
    public float getWidth() {
        return width;
    }
}
