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

package org.terasology.cities.model.roof;

import org.terasology.engine.world.block.BlockArea;
import org.terasology.engine.world.block.BlockAreac;

/**
 * A roof with rectangular shape
 */
public class RectangularRoof extends AbstractRoof {

    private BlockArea shape = new BlockArea(BlockArea.INVALID);
    private BlockArea baseRect = new BlockArea(BlockArea.INVALID);

    /**
     * @param baseRect the building rectangle (must be fully inside <code>withEaves</code>).
     * @param withEaves the roof area including eaves (=overhang)
     * @param baseHeight the base height of the roof
     */
    public RectangularRoof(BlockAreac baseRect, BlockAreac withEaves, int baseHeight) {
        super(baseHeight);
        this.shape.set(withEaves);
        this.baseRect.set(baseRect);
    }

    /**
     * @return the roof area including eaves
     */
    public BlockAreac getBaseArea() {
        return baseRect;
    }

    /**
     * @return the roof area including eaves
     */
    public BlockAreac getArea() {
        return shape;
    }
}
