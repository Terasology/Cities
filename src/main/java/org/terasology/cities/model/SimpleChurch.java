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

package org.terasology.cities.model;

import java.util.Set;

import com.google.common.collect.Sets;

/**
 * A simple aisle-less church consisting of nave and bell tower 
 * @author Martin Steiger
 */
public class SimpleChurch extends MultipartBuilding {

    private final SimpleDoor door;
    private SimpleBuildingPart tower;
    private SimpleBuildingPart nave;

    /**
     * @param bellTower the bell tower
     * @param nave the nave
     * @param entrance the entrance door
     */
    public SimpleChurch(SimpleBuildingPart nave, SimpleBuildingPart bellTower, SimpleDoor entrance) {
        this.tower = bellTower;
        this.nave = nave;
        this.door = entrance; 

        addPart(tower);
        addPart(nave);
    }

    /**
     * @return the tower
     */
    public SimpleBuildingPart getTower() {
        return this.tower;
    }


    /**
     * @return the nave
     */
    public SimpleBuildingPart getNave() {
        return this.nave;
    }

    /**
     * @return the door
     */
    public SimpleDoor getDoor() {
        return this.door;
    }

}
