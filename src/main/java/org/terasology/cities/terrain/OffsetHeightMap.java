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

/**
 * An implementation that returns other.height + offset
 * @author Martin Steiger
 */
public class OffsetHeightMap extends HeightMapAdapter {

    private final int offset;
    private final HeightMap base;

    /**
     * @param base the base height map
     * @param offset the height offset
     */
    public OffsetHeightMap(HeightMap base, int offset) {
        this.base = base;
        this.offset = offset;
    }

    @Override
    public int apply(int x, int z) {
        return base.apply(x, z) + offset;
    }


}
