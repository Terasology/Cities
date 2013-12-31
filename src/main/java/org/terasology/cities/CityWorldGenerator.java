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

package org.terasology.cities;

import org.terasology.cities.terrain.NoiseHeightMap;
import org.terasology.core.world.generator.AbstractBaseWorldGenerator;
import org.terasology.engine.SimpleUri;
import org.terasology.world.generator.RegisterWorldGenerator;

/**
 * @author Martin Steiger
 */
@RegisterWorldGenerator(id = "city", displayName = "City World")
public class CityWorldGenerator extends AbstractBaseWorldGenerator {

    private NoiseHeightMap heightMap;

    /**
     * @param uri the uri
     */
    public CityWorldGenerator(SimpleUri uri) {
        super(uri);
    }

    @Override
    public void initialize() {

        heightMap = new NoiseHeightMap();
        
        register(new HeightMapTerrainGenerator(heightMap));
//        register(new BoundaryGenerator(heightMap));
        register(new CityTerrainGenerator(heightMap));
        register(new FloraGeneratorFast(heightMap));
    }
    
    @Override
    public void setWorldSeed(String seed) {
        if (seed == null) {
            return;
        }
        
        heightMap.setSeed(seed);
        
        super.setWorldSeed(seed);
    }
}
