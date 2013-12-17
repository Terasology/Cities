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

package org.terasology.cities;

import java.util.Collections;
import java.util.Map;

import org.terasology.cities.terrain.HeightMap;
import org.terasology.world.WorldBiomeProvider;
import org.terasology.world.chunks.Chunk;
import org.terasology.world.generator.FirstPassGenerator;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class CityTerrainGenerator implements FirstPassGenerator {

    private MyGenerator generator;
    private HeightMap heightMap;

    // private WorldBiomeProvider worldBiomeProvider;

    /**
     * @param heightMap the height map to use
     */
    public CityTerrainGenerator(HeightMap heightMap) {
        this.heightMap = heightMap;
    }

    @Override
    public void setWorldSeed(String worldSeed) {

        generator = new MyGenerator(worldSeed, heightMap);
    }

    @Override
    public void setWorldBiomeProvider(WorldBiomeProvider worldBiomeProvider) {
//        this.worldBiomeProvider = worldBiomeProvider;
    }

    /**
     * Not sure what this method does - it does not seem to be used though
     */
    @Override
    public Map<String, String> getInitParameters() {
        return Collections.emptyMap();
    }

    /**
     * Not sure what this method does - it does not seem to be used though
     */
    @Override
    public void setInitParameters(Map<String, String> initParameters) {
        // ignore
    }

    @Override
    public void generateChunk(Chunk chunk) {
        if (generator == null) {
            throw new IllegalStateException("seed has not been set");
        }
        
        generator.writeChunk(chunk);
    }
}
