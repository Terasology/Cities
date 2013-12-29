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

package org.terasology.cities.model;

import java.awt.Rectangle;

/**
 * A simple tower
 * @author Martin Steiger
 */
public class SimpleTower extends SimpleBuilding implements Tower {

    /**
     * @param layout the building layout
     * @param baseHeight the height of the floor level
     * @param wallHeight the building height above the floor level
     */
    public SimpleTower(Rectangle layout, int baseHeight, int wallHeight) {
        super(layout, new BattlementRoof(new Rectangle(layout.x - 1, layout.y - 1, layout.width + 2, layout.height + 2), baseHeight + wallHeight, 1), baseHeight, wallHeight);
    }

    
}
