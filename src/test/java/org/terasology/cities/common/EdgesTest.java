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

package org.terasology.cities.common;

import org.junit.Assert;
import org.junit.Test;
import org.terasology.commonworld.Orientation;
import org.terasology.math.geom.LineSegment;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;

/**
 * Tests the {@link Edges} class.
 */
public class EdgesTest {

    @Test
    public void testCorners() {
        Vector2i min = new Vector2i(10, 5);
        Vector2i max = new Vector2i(20, 30);
        Rect2i rc = Rect2i.createFromMinAndMax(min, max);

        Assert.assertEquals(new Vector2i(20, 5), Edges.getCorner(rc, Orientation.NORTHEAST));
        Assert.assertEquals(new Vector2i(15, 5), Edges.getCorner(rc, Orientation.NORTH));
        Assert.assertEquals(new Vector2i(10, 5), Edges.getCorner(rc, Orientation.NORTHWEST));
        Assert.assertEquals(new Vector2i(10, 17), Edges.getCorner(rc, Orientation.WEST));
        Assert.assertEquals(new Vector2i(10, 30), Edges.getCorner(rc, Orientation.SOUTHWEST));
        Assert.assertEquals(new Vector2i(15, 30), Edges.getCorner(rc, Orientation.SOUTH));
        Assert.assertEquals(new Vector2i(20, 30), Edges.getCorner(rc, Orientation.SOUTHEAST));
        Assert.assertEquals(new Vector2i(20, 17), Edges.getCorner(rc, Orientation.EAST));
    }

    @Test
    public void testEdges() {
        Vector2i min = new Vector2i(10, 5);
        Vector2i max = new Vector2i(20, 30);
        Rect2i rc = Rect2i.createFromMinAndMax(min, max);

        Assert.assertEquals(new LineSegment(10, 5, 20, 5), Edges.getEdge(rc, Orientation.NORTH));
        Assert.assertEquals(new LineSegment(20, 30, 10, 30), Edges.getEdge(rc, Orientation.SOUTH));
        Assert.assertEquals(new LineSegment(10, 30, 10, 5), Edges.getEdge(rc, Orientation.WEST));
        Assert.assertEquals(new LineSegment(20, 5, 20, 30), Edges.getEdge(rc, Orientation.EAST));

        // not sure how much sense that makes ..
        Assert.assertEquals(new LineSegment(15, 5, 20, 17), Edges.getEdge(rc, Orientation.NORTHEAST));
    }
}
