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

package org.terasology.cities.bldg;

import org.terasology.commonworld.Orientation;

/**
 * A simple aisle-less church consisting of nave and bell tower
 */
public class SimpleChurch extends DefaultBuilding {

    private final SimpleDoor door;
    private BuildingPart tower;
    private BuildingPart nave;

    /**
     * @param bellTower the bell tower
     * @param nave the nave
     * @param entrance the entrance door
     */
    public SimpleChurch(Orientation orient, BuildingPart nave, BuildingPart bellTower, SimpleDoor entrance) {
        super(orient);
        this.tower = bellTower;
        this.nave = nave;
        this.door = entrance;

        addPart(bellTower);
        addPart(nave);
    }

    /**
     * @return the tower
     */
    public BuildingPart getTower() {
        return this.tower;
    }


    /**
     * @return the nave
     */
    public BuildingPart getNave() {
        return this.nave;
    }

    /**
     * @return the door
     */
    public SimpleDoor getDoor() {
        return this.door;
    }

}
