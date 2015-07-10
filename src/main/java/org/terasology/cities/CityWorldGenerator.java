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
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.spawner.FixedSpawner;
import org.terasology.logic.spawner.Spawner;
import org.terasology.math.geom.Vector3f;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.generation.World;
import org.terasology.world.generation.WorldBuilder;
import org.terasology.world.generator.RegisterWorldGenerator;
import org.terasology.world.generator.WorldConfigurator;
import org.terasology.world.generator.plugin.WorldGeneratorPluginLibrary;

import java.util.Map;

@RegisterWorldGenerator(id = "city", displayName = "City World")
public class CityWorldGenerator extends AbstractBaseWorldGenerator {

    World world;

    private final Spawner spawner = new FixedSpawner(0, 0);

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

        WorldBuilder worldBuilder = new WorldBuilder(CoreRegistry.get(WorldGeneratorPluginLibrary.class))
                .addProvider(new HeightMapCompatibilityFacetProvider(heightMap))
                .addProvider(new SeaLevelProvider(2))
                .addProvider(new World2dPreviewProvider());
        worldBuilder.setSeed(seed.hashCode());
        world = worldBuilder.build();

        super.setWorldSeed(seed);
    }

    @Override
    public WorldConfigurator getConfigurator() {

        WorldConfigurator wc = new WorldConfigurator() {

            @Override
            public Map<String, Component> getProperties() {
                Map<String, Component> map = Maps.newHashMap();
                map.put("Terrain", new CityTerrainComponent());
                map.put("Spawning", new CitySpawnComponent());
                return map;
            }

            @Override
            public void setProperty(String key, Component comp) {
                // TODO Auto-generated method stub
            }

        };

        return wc;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public Vector3f getSpawnPosition(EntityRef entity) {
        return spawner.getSpawnPosition(getWorld(), entity);
    }

}
