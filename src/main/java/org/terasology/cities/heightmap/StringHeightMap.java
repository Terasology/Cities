/*
 * Copyright 2014 MovingBlocks
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

package org.terasology.cities.heightmap;

import java.util.List;

/**
 * Reads heightmap information from a list of strings
 * @author Martin Steiger
 */
class StringHeightMap extends HeightMapAdapter {

    private final List<String> data;
    
    /**
     * @param data the list of string
     */
    public StringHeightMap(List<String> data) {
        this.data = data;
    }

    @Override
    public int apply(int x, int z) {
        return data.get(z).codePointAt(x);
    }

}
