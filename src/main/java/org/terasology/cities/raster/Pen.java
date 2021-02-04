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

import org.joml.Vector2ic;
import org.terasology.world.block.BlockAreac;

/**
 * Assigns blocks based on x and z values
 */
public interface Pen {

    /**
     * @param x the x coordinate
     * @param z the z coordinate
     */
    void draw(int x, int z);

    default void draw(Vector2ic p) {
        draw(p.x(), p.y());
    }

    /**
     * @return the valid target area
     */
    BlockAreac getTargetArea();
}
