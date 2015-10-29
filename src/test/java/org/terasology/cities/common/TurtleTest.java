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
import org.terasology.cities.bldg.gen.Turtle;
import org.terasology.commonworld.Orientation;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;

/**
 * Tests the {@link Turtle} class.
 */
public class TurtleTest {

    /**
     * <pre>
     * o
     *
     * +---+
     * |   |
     * |   |
     * |   |
     * |   |
     * +---+
     * </pre>
     */
    private Rect2i rect = Rect2i.createFromMinAndMax(0, 10, 50, 100);

    @Test
    public void testMoveSouth() {
        Vector2i pos = Edges.getCorner(rect, Orientation.NORTH);
        Turtle cur = new Turtle(pos, Orientation.SOUTH);
        cur.move(-5, 10);
        Assert.assertEquals(new Vector2i(30, 20), cur.getPos());
        Assert.assertEquals(Rect2i.createFromMinAndSize(19, 22, 14, 18), cur.rect(-2, 2, 14, 18));
    }

    @Test
    public void testMoveEast() {
        Vector2i pos = Edges.getCorner(rect, Orientation.WEST);
        Turtle cur = new Turtle(pos, Orientation.EAST);
        cur.move(-5, 10);
        Assert.assertEquals(new Vector2i(10, 50), cur.getPos());
        Assert.assertEquals(Rect2i.createFromMinAndSize(12, 48, 18, 14), cur.rect(-2, 2, 14, 18));
    }

    @Test
    public void testMoveNorth() {
        Vector2i pos = Edges.getCorner(rect, Orientation.SOUTH);
        Turtle cur = new Turtle(pos, Orientation.NORTH);
        cur.move(-5, 10);
        Assert.assertEquals(new Vector2i(20, 90), cur.getPos());
        Assert.assertEquals(Rect2i.createFromMinAndSize(18, 71, 14, 18), cur.rect(-2, 2, 14, 18));
    }

    @Test
    public void testMoveWest() {
        Vector2i pos = Edges.getCorner(rect, Orientation.EAST);
        Turtle cur = new Turtle(pos, Orientation.WEST);
        cur.move(-5, 10);
        Assert.assertEquals(new Vector2i(40, 60), cur.getPos());
        Assert.assertEquals(Rect2i.createFromMinAndSize(21, 49, 18, 14), cur.rect(-2, 2, 14, 18));
    }
}
