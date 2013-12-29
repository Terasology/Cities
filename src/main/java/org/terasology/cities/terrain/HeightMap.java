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

package org.terasology.cities.terrain;

import org.terasology.math.Vector2i;

import com.google.common.base.Function;

/**
 * Definition of a height map
 * @author Martin Steiger
 */
public interface HeightMap extends Function<Vector2i, Integer> {

    /**
     * @param x the x world coord
     * @param z the z world coord
     * @return the height
     */
    int apply(int x, int z);

}
