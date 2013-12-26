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
