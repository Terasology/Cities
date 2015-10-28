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

import java.util.Set;

import org.terasology.cities.door.Door;
import org.terasology.cities.model.roof.Roof;
import org.terasology.cities.window.Window;
import org.terasology.math.geom.Shape;

/**
 * Defines a part of a building.
 * This is similar to an entire building.
 */
public interface BuildingPart {

    /**
     * @return the building layout
     */
    Shape getShape();

    Roof getRoof();

    int getWallHeight();

    int getBaseHeight();

    Set<Window> getWindows();
    Set<Door> getDoors();
}
