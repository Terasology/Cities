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

package org.terasology.cities.model.bldg;

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
