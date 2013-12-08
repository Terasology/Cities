/*
 * Copyright 2013 MovingBlocks
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.terasology.world.generator.city.model;

import java.util.Collections;
import java.util.List;

import javax.vecmath.Point2d;

import com.google.common.collect.Lists;

/**
 * A road contains a start and an end junction point and a list of points between them.
 * The point list goes from start to end, but does not contain the start and end points.
 * @author Martin Steiger
 */
public class Road {

    private final List<Point2d> points = Lists.newArrayList();
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
    public void add(Point2d pt) {
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
    public List<Point2d> getPoints() {
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
        return "Road [" + start + " -> " + end + ", " + points.size() + " segments]";
    }
}
