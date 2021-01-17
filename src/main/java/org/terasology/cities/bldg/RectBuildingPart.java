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

import org.terasology.cities.bldg.shape.RectangularBase;
import org.terasology.cities.model.roof.Roof;
import org.terasology.world.block.BlockArea;
import org.terasology.world.block.BlockAreac;

/**
 *
 */
public class RectBuildingPart extends AbstractBuildingPart implements RectangularBase {

    private final BlockArea layout = new BlockArea(BlockArea.INVALID);

    public RectBuildingPart(BlockAreac layout,Roof roof, int baseHeight, int wallHeight) {
        super(roof, baseHeight, wallHeight);
        this.layout.set(layout);
    }

    @Override
    public BlockAreac getShape() {
        return this.layout;
    }
}
