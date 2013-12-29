/*
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.cities.common;

import static org.junit.Assert.assertEquals;
import static org.terasology.cities.common.Orientation.EAST;
import static org.terasology.cities.common.Orientation.NORTH;
import static org.terasology.cities.common.Orientation.NORTHEAST;
import static org.terasology.cities.common.Orientation.NORTHWEST;
import static org.terasology.cities.common.Orientation.SOUTH;
import static org.terasology.cities.common.Orientation.SOUTHEAST;
import static org.terasology.cities.common.Orientation.SOUTHWEST;
import static org.terasology.cities.common.Orientation.WEST;

import org.junit.Test;

/**
 * Tests {@link Orientation}
 * @author Martin Steiger
 */
@SuppressWarnings("javadoc")
public class OrientationTest {

    @Test
    public void testOpposites() {
        assertEquals(SOUTH, NORTH.getOpposite());
        assertEquals(NORTH, SOUTH.getOpposite());
        assertEquals(EAST, WEST.getOpposite());
        assertEquals(WEST, EAST.getOpposite());
        assertEquals(NORTHEAST, SOUTHWEST.getOpposite());
        assertEquals(SOUTHWEST, NORTHEAST.getOpposite());
        assertEquals(NORTHWEST, SOUTHEAST.getOpposite());
        assertEquals(SOUTHEAST, NORTHWEST.getOpposite());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalid() {
        NORTH.getRotated(12);
    }

    @Test
    public void testRotationClockwise() {
        assertEquals(WEST, SOUTH.getRotated(90));
        assertEquals(SOUTH, EAST.getRotated(90));
        assertEquals(EAST, NORTH.getRotated(90));
        assertEquals(NORTH, WEST.getRotated(90));
    }

    @Test
    public void testRotationCounterClockwise() {
        assertEquals(SOUTH, WEST.getRotated(-90));
        assertEquals(EAST, SOUTH.getRotated(-90));
        assertEquals(NORTH, EAST.getRotated(-90));
        assertEquals(WEST, NORTH.getRotated(-90));
    }
    
    @Test
    public void testRotationModulo() {
        assertEquals(NORTH, SOUTH.getRotated(180));
        assertEquals(NORTH, SOUTH.getRotated(720 + 180));
        assertEquals(NORTH, SOUTH.getRotated(-180));
        assertEquals(NORTH, SOUTH.getRotated(-720 - 180));
        
        assertEquals(NORTH, NORTH.getRotated(0));
        assertEquals(NORTH, NORTH.getRotated(360));
        assertEquals(NORTH, NORTH.getRotated(3600));
        assertEquals(NORTH, NORTH.getRotated(-3600));
    }
}
