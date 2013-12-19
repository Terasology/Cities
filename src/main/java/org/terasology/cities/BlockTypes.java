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

package org.terasology.cities;

/**
 * A set of constants used for city building
 * @author Martin Steiger
 */
public final class BlockTypes {

    /**
     * Air (maybe not required)
     */
    public static final String AIR = "air";

    /**
     * Surface of a road (asphalt)
     */
    public static final String ROAD_SURFACE = "road:surface";
    
    /**
     * Empty space in a lot
     */
    public static final String LOT_EMPTY = "lot:empty";

    /**
     * A simple building's floor
     */
    public static final String BUILDING_FLOOR = "building:floor";

    /**
     * A simple building's foundation
     */
    public static final String BUILDING_FOUNDATION = "building:foundation";

    /**
     * A simple building wall
     */
    public static final String BUILDING_WALL = "building:wall";

    /**
     * Flat roof
     */
    public static final String ROOF_FLAT = "roof:flat";

    /**
     * Hip roof
     */
    public static final String ROOF_HIP = "roof:hip";

    /**
     * Dome roof
     */
    public static final String ROOF_DOME = "roof:dome";

    /**
     * The roof gable for saddle roofs
     */
    public static final String ROOF_GABLE = "roof:gable";

    /**
     * Saddle roof
     */
    public static final String ROOF_SADDLE = "roof:saddle";

    /**
     * Fence along top (east-west)
     */
    public static final String FENCE_TOP = "Fences:Fence.front";

    /**
     * Fence along bottom (east-west)
     */
    public static final String FENCE_BOTTOM = "Fences:Fence.back";

    /**
     * Fence along left (north-south)
     */
    public static final String FENCE_LEFT = "Fences:Fence.left";

    /**
     * Fence along right (north-south)
     */
    public static final String FENCE_RIGHT = "Fences:Fence.right";

    /**
     * Fence corner (south-west)
     */
    public static final String FENCE_SW = "fences:corner:sw";

    /**
     * Fence corner (south-east)
     */
    public static final String FENCE_SE = "fences:corner:se";

    /**
     * Fence corner (north-west)
     */
    public static final String FENCE_NW = "fences:corner:nw";

    /**
     * Fence corner (north-east)
     */
    public static final String FENCE_NE = "fences:corner:ne";

    public static final String FENCE_GATE_TOP = "fences:gate:top";
    public static final String FENCE_GATE_LEFT = "fences:gate:left";
    public static final String FENCE_GATE_RIGHT = "fences:gate:right";
    public static final String FENCE_GATE_BOTTOM = "fences:gate:bottom";

    private BlockTypes() {
        // private constructor
    }

}
