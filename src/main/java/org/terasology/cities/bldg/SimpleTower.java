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

import org.joml.Vector2i;
import org.terasology.cities.common.Edges;
import org.terasology.cities.door.SimpleDoor;
import org.terasology.cities.model.roof.BattlementRoof;
import org.terasology.cities.model.roof.Roof;
import org.terasology.commonworld.Orientation;
import org.terasology.engine.world.block.BlockArea;
import org.terasology.engine.world.block.BlockAreac;

/**
 * A simple tower
 */
public class SimpleTower extends DefaultBuilding implements Tower {

    private BlockArea shape = new BlockArea(BlockArea.INVALID);
    private RectBuildingPart room;

    /**
     * @param orient the orientation of the building
     * @param layout the building layout
     * @param baseHeight the height of the floor level
     * @param wallHeight the building height above the floor level
     */
    public SimpleTower(Orientation orient, BlockAreac layout, int baseHeight, int wallHeight) {
        super(orient);
        this.shape.set(layout);

        BlockArea roofArea = layout.expand(1, 1,new BlockArea(BlockArea.INVALID));
        Roof roof = new BattlementRoof(layout, roofArea, baseHeight + wallHeight, 1);
        room = new StaircaseBuildingPart(layout, orient, roof, baseHeight, wallHeight);
        Vector2i doorPos = new Vector2i(Edges.getCorner(layout, orient));
        room.addDoor(new SimpleDoor(orient, doorPos, baseHeight, baseHeight + 2));
        addPart(room);
    }

    public BlockAreac getShape() {
        return shape;
    }

    public RectBuildingPart getStaircase() {
        return room;
    }

}
