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

import org.terasology.math.TeraMath;
import org.terasology.utilities.procedural.SimplexNoise;

/**
 * A simple implementation based on {@link SimplexNoise}
 * @author Martin Steiger
 */
public class NoiseHeightMap extends HeightMapAdapter {

    private SimplexNoise terrainNoise = new SimplexNoise(0);

    /**
     * @param seed the seed value
     */
    public void setSeed(String seed) {
        terrainNoise = new SimplexNoise(seed.hashCode());
    }
    
    @Override
    public int apply(int x, int z) {
        int val = 5 + (int) (terrainNoise.noise(x / 155d, z / 155d) * 8d);
        val += (int) (terrainNoise.noise(x / 72d, z / 72d) * 4d);
        
        return TeraMath.clamp(val, 1, 12);
    }


}
