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

import java.awt.Rectangle;

import org.terasology.cities.model.roof.Roof;

/**
 * Defines a simple housing with only one room
 * @author Martin Steiger
 */
public class SimpleHome extends SimpleBuilding {

    private SimpleDoor door;

    /**
     * @param layout the building layout
     * @param roof the roof definition
     * @param door the door area in one of the wall
     * @param baseHeight the height of the floor level
     * @param wallHeight the building height above the floor level
     */
    public SimpleHome(Rectangle layout, Roof roof, int baseHeight, int wallHeight, SimpleDoor door) {
        super(layout, roof, baseHeight, wallHeight);
        
        // TODO: check that the door is at the border and heights are ok
        
        this.door = door;
    }

    /**
     * @return the door area in one of the wall
     */
    public SimpleDoor getDoor() {
        return door;
    }
    
}
