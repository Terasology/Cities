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

import java.math.RoundingMode;

import org.terasology.cities.common.Edges;
import org.terasology.cities.door.SimpleDoor;
import org.terasology.cities.model.roof.Roof;
import org.terasology.commonworld.Orientation;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;

/**
 * Defines a rectangular shaped building in the most common sense
 */
public class SimpleRectHouse extends DefaultBuilding {

    private Rect2i shape;
    private DefaultBuildingPart room;

    /**
     * @param orient the orientation of the building
     * @param layout the building layout
     * @param roof the roof definition
     * @param baseHeight the height of the floor level
     * @param wallHeight the building height above the floor level
     */
    public SimpleRectHouse(Orientation orient, Rect2i layout, Roof roof, int baseHeight, int wallHeight) {
        super(orient);

        this.shape = layout;
        room = new DefaultBuildingPart(layout, roof, baseHeight, wallHeight);
        Vector2i doorPos = new Vector2i(Edges.getEdge(layout, orient).lerp(0.5f), RoundingMode.HALF_UP);
        room.addDoor(new SimpleDoor(orient, doorPos, baseHeight, baseHeight + 2));
        addPart(room);
    }

    public Rect2i getShape() {
        return shape;
    }

    public DefaultBuildingPart getRoom() {
        return room;
    }
}
