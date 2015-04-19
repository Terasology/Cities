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

package org.terasology.cities.model;

import java.util.Collections;
import java.util.List;

import org.terasology.math.Vector2i;

import com.google.common.collect.Lists;

/**
 * A road contains a start and an end junction point and a list of points between them.
 * The point list goes from start to end, but does not contain the start and end points.
 */
public class Road {

    private final List<Vector2i> points = Lists.newArrayList();
    private final Junction end;
    private final Junction start;
    private double width = 1.0f;

    /**
     * The point list goes from start to end, but does not contain them
     * @param start the start point
     * @param end the end point
     */
    public Road(Junction start, Junction end) {
        this.start = start;
        this.end = end;
        
        start.addRoad(this);
        end.addRoad(this);
    }

    /**
     * @param width the width of the road in blocks
     */
    public void setWidth(double width) {
        this.width = width;
    }
    
    /**
     * @param pt a point on the road
     */
    public void add(Vector2i pt) {
        points.add(pt);
    }
    
    /**
     * @return the end
     */
    public Junction getEnd() {
        return end;
    }

    /**
     * @return the start
     */
    public Junction getStart() {
        return start;
    }

    /**
     * @return an unmodifiable view on the segment points (can be empty, but never <code>null</code>)
     */
    public List<Vector2i> getPoints() {
        return Collections.unmodifiableList(points);
    }
    
    /**
     * @return the width of the road in blocks
     */
    public double getWidth() {
        return width;
    }
    
    @Override
    public String toString() {
        return "Road [" + start + " -> " + end + ", " + (points.size() + 1) + " segments]";
    }
}
