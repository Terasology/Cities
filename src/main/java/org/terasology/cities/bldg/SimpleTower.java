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

import org.terasology.cities.model.roof.BattlementRoof;
import org.terasology.commonworld.Orientation;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;

/**
 * A simple tower
 */
public class SimpleTower extends SimpleRectHouse implements Tower {

    /**
     * @param orient the orientation of the building
     * @param layout the building layout
     * @param baseHeight the height of the floor level
     * @param wallHeight the building height above the floor level
     */
    public SimpleTower(Orientation orient, Rect2i layout, int baseHeight, int wallHeight) {
        super(orient, layout, new BattlementRoof(layout, layout.expand(new Vector2i(2, 2)),
                baseHeight + wallHeight, 1), baseHeight, wallHeight);
    }


}
