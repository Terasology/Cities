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

import java.util.Map;

import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.commonworld.heightmap.HeightMaps;
import org.terasology.commonworld.heightmap.NoiseHeightMap;
import org.terasology.commonworld.symmetry.Symmetries;
import org.terasology.core.world.generator.AbstractBaseWorldGenerator;
import org.terasology.engine.SimpleUri;
import org.terasology.entitySystem.Component;
import org.terasology.world.generator.RegisterWorldGenerator;
import org.terasology.world.generator.WorldConfigurator;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

/**
 * @author Martin Steiger
 */
@RegisterWorldGenerator(id = "city", displayName = "City World")
public class CityWorldGenerator extends AbstractBaseWorldGenerator {

    private NoiseHeightMap noiseMap;
    private HeightMap heightMap;
    
    /**
     * @param uri the uri
     */
    public CityWorldGenerator(SimpleUri uri) {
        super(uri);
    }

    @Override
    public void initialize() {

        noiseMap = new NoiseHeightMap();
        heightMap = HeightMaps.symmetric(noiseMap, Symmetries.alongNegativeDiagonal());
        
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
        
        if (heightMap == null) {
            noiseMap = new NoiseHeightMap();
            heightMap = HeightMaps.symmetric(noiseMap, Symmetries.alongNegativeDiagonal());
        }
        
        noiseMap.setSeed(seed);
        
        super.setWorldSeed(seed);
    }

    @Override
    public Optional<WorldConfigurator> getConfigurator() {

        WorldConfigurator wc = new WorldConfigurator() {

            @Override
            public Map<String, Component> getProperties() {
                Map<String, Component> map = Maps.newHashMap();
                map.put("Terrain", new CityTerrainComponent());
                map.put("Spawning", new CitySpawnComponent());
                return map;
            }

        };

        return Optional.of(wc);
    }    
}
