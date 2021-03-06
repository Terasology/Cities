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

package org.terasology.cities.raster;

import org.terasology.engine.world.block.BlockArea;
import org.terasology.engine.world.block.BlockAreac;

/**
 * A {@link Pen} that stores the valid target area.
 */
public abstract class AbstractPen implements Pen {

    private final BlockAreac targetArea;

    /**
     * @param targetArea the valid area
     */
    public AbstractPen(BlockAreac targetArea) {
        this.targetArea = new BlockArea(targetArea);
    }

    @Override
    public BlockAreac getTargetArea() {
        return targetArea;
    }
}
