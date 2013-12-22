/*
 * Copyright 2013 MovingBlocks
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY, without even the implied warranty of
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
public enum BlockTypes {

    /**
     * Air (maybe not required)
     */
    AIR,

    /**
     * Surface of a road (asphalt)
     */
    ROAD_SURFACE,
    
    /**
     * Empty space in a lot
     */
    LOT_EMPTY,

    /**
     * A simple building's floor
     */
    BUILDING_FLOOR,

    /**
     * A simple building's foundation
     */
    BUILDING_FOUNDATION,

    /**
     * A simple building wall
     */
    BUILDING_WALL,

    /**
     * Flat roof
     */
    ROOF_FLAT,

    /**
     * Hip roof
     */
    ROOF_HIP,

    /**
     * Dome roof
     */
    ROOF_DOME,

    /**
     * The roof gable for saddle roofs
     */
    ROOF_GABLE,

    /**
     * Saddle roof
     */
    ROOF_SADDLE,

    /**
     * Tower stone
     */
    TOWER_WALL,

    /**
     * Fence along top (east-west)
     */
    FENCE_TOP,

    /**
     * Fence along bottom (east-west)
     */
    FENCE_BOTTOM,

    /**
     * Fence along left (north-south)
     */
    FENCE_LEFT,

    /**
     * Fence along right (north-south)
     */
    FENCE_RIGHT,

    /**
     * Fence corner (south-west)
     */
    FENCE_SW,

    /**
     * Fence corner (south-east)
     */
    FENCE_SE,

    /**
     * Fence corner (north-west)
     */
    FENCE_NW,

    /**
     * Fence corner (north-east)
     */
    FENCE_NE,

    FENCE_GATE_TOP,
    FENCE_GATE_LEFT,
    FENCE_GATE_RIGHT,
    FENCE_GATE_BOTTOM;

}
