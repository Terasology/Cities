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

import org.joml.Circlef;
import org.joml.Vector2ic;
import org.terasology.cities.model.roof.ConicRoof;
import org.terasology.commonworld.Orientation;

/**
 * A round house with a conic roof
 */
public class SimpleRoundHouse extends DefaultBuilding {

    private Circlef layout;
    private RoundBuildingPart room;

    /**
     * @param orient the orientation of the building
     * @param center the center of the tower
     * @param radius the radius
     * @param baseHeight the height of the floor level
     * @param wallHeight the building height above the floor level
     */
    public SimpleRoundHouse(Orientation orient, Vector2ic center, int radius, int baseHeight, int wallHeight) {
        super(orient);

        layout = new Circlef(center.x(), center.y(), radius);
        room = new RoundBuildingPart(
            layout,
            new ConicRoof(center, radius + 1, baseHeight + wallHeight, 1),
            baseHeight,
            wallHeight);
        addPart(room);
    }

    public Circlef getShape() {
        return layout;
    }

    public RoundBuildingPart getRoom() {
        return room;
    }
}
