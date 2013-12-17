/*
 * Copyright 2013 MovingBlocks
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.terasology.cities.terrain;

import org.terasology.math.TeraMath;
import org.terasology.math.Vector2i;
import org.terasology.utilities.procedural.SimplexNoise;

/**
 * A simple implementation based on {@link SimplexNoise}
 * @author Martin Steiger
 */
public class NoiseHeightMap implements HeightMap {

    private SimplexNoise terrainNoise = new SimplexNoise(0);

    /**
     * @param seed the seed value
     */
    public void setSeed(String seed) {
        terrainNoise = new SimplexNoise(seed.hashCode());
    }
    
    @Override
    public Integer apply(Vector2i input) {
        return apply(input.x, input.y);
    }

    @Override
    public int apply(int x, int z) {
        int val = 5 + (int) (terrainNoise.noise(x / 155d, z / 155d) * 8d);
        val += (int) (terrainNoise.noise(x / 72d, z / 72d) * 4d);
        
        return TeraMath.clamp(val, 1, 12);
    }


}
