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

import org.terasology.math.Vector2i;


/**
 * A combination of {@link HeightMapAdapter} and {@link SymmetricHeightMap}
 * @author Martin Steiger
 */
public abstract class AbstractSymmetricHeightMap extends HeightMapAdapter implements SymmetricHeightMap {

    private final HeightMap heightMap;

    /**
     * @param hm the underlying height map
     */
    public AbstractSymmetricHeightMap(HeightMap hm) {
        this.heightMap = hm;
    }
    
    @Override
    public int apply(int x, int z) {
        if (isMirrored(x, z)) {
            return heightMap.apply(getMirrored(x, z));
        } else {
            return heightMap.apply(x, z);            
        }
    }

    @Override
    public boolean isMirrored(Vector2i v) {
        return isMirrored(v.x, v.y);
    }
    
    @Override
    public Vector2i getMirrored(Vector2i v) {
        return getMirrored(v.x, v.y);
    }

}
