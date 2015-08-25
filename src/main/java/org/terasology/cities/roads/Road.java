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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.ImmutableVector2i;

import com.google.common.collect.Lists;

/**
 * A road contains two end points and a list of points between them.
 */
public class Road {

    private final List<ImmutableVector2i> segmentPoints = Lists.newArrayList();
    private float width = 1.0f;

    /**
     * The point list goes from start to end, but does not contain them
     * @param end0 one end point
     * @param end1 the other end point
     * @param width the width of the road
     */
    public Road(BaseVector2i end0, BaseVector2i end1, float width) {
        this(Arrays.asList(end0, end1), width);
    }

    public Road(List<? extends BaseVector2i> segPoints, float width) {
        for (BaseVector2i segPoint : segPoints) {
            segmentPoints.add(ImmutableVector2i.createOrUse(segPoint));
        }
        this.width = width;
    }

    /**
     * @return the other end point
     */
    public ImmutableVector2i getEnd1() {
        return segmentPoints.get(segmentPoints.size() - 1);
    }

    /**
     * @return one end point
     */
    public ImmutableVector2i getEnd0() {
        return segmentPoints.get(0);
    }

    /**
     * @return an unmodifiable view on the segment points (can be empty, but never <code>null</code>)
     */
    public List<ImmutableVector2i> getPoints() {
        return Collections.unmodifiableList(segmentPoints);
    }

    /**
     * @return the width of the road in blocks
     */
    public float getWidth() {
        return width;
    }
}
