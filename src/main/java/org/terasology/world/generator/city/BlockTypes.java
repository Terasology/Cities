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

package org.terasology.world.generator.city;

/**
 * A set of constants used for city building
 * @author Martin Steiger
 */
public final class BlockTypes {

    /**
     * Surface of a road (asphalt)
     */
    public static final String ROAD_SURFACE = "road:surface";
    
    /**
     * Empty space in a lot
     */
    public static final String LOT_EMPTY = "lot:empty";

    /**
     * A simple building wall
     */
    public static final String BUILDING_FLOOR = "building:floor";

    /**
     * A simple building wall
     */
    public static final String BUILDING_WALL = "building:wall";

    /**
     * Flat roof
     */
    public static final String ROOF_FLAT = "roof:flat";
    
    private BlockTypes() {
        // private constructor
    }

}
