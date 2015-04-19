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

package org.terasology.cities;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.commonworld.heightmap.HeightMaps;
import org.terasology.commonworld.heightmap.NoiseHeightMap;
import org.terasology.commonworld.symmetry.Symmetries;
import org.terasology.core.world.generator.AbstractBaseWorldGenerator;
import org.terasology.core.world.generator.facetProviders.SeaLevelProvider;
import org.terasology.core.world.generator.facetProviders.World2dPreviewProvider;
import org.terasology.engine.SimpleUri;
import org.terasology.entitySystem.Component;
import org.terasology.world.generation.World;
import org.terasology.world.generation.WorldBuilder;
import org.terasology.world.generator.RegisterWorldGenerator;
import org.terasology.world.generator.WorldConfigurator;

import java.util.Map;

@RegisterWorldGenerator(id = "city", displayName = "City World")
public class CityWorldGenerator extends AbstractBaseWorldGenerator {

    World world;
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

        world.initialize();
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

        world = new WorldBuilder(0)
                .addProvider(new HeightMapCompatibilityFacetProvider(heightMap))
                .addProvider(new SeaLevelProvider(2))
                .addProvider(new World2dPreviewProvider())
                .build();
        
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

    @Override
    public World getWorld() {
        return world;
    }
}
