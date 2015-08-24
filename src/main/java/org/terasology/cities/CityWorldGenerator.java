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

import org.terasology.cities.lakes.LakeFacetProvider;
import org.terasology.cities.sites.SettlementFacetProvider;
import org.terasology.cities.surface.SurfaceHeightFacetProvider;
import org.terasology.cities.surface.InfiniteSurfaceHeightFacetProvider;
import org.terasology.core.world.generator.facetProviders.DefaultFloraProvider;
import org.terasology.core.world.generator.facetProviders.EnsureSpawnableChunkZeroProvider;
import org.terasology.core.world.generator.facetProviders.SeaLevelProvider;
import org.terasology.core.world.generator.rasterizers.FloraRasterizer;
import org.terasology.core.world.generator.rasterizers.SolidRasterizer;
import org.terasology.engine.SimpleUri;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.spawner.FixedSpawner;
import org.terasology.logic.spawner.Spawner;
import org.terasology.math.geom.Vector3f;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.generation.BaseFacetedWorldGenerator;
import org.terasology.world.generation.World;
import org.terasology.world.generation.WorldBuilder;
import org.terasology.world.generator.RegisterWorldGenerator;
import org.terasology.world.generator.plugin.WorldGeneratorPluginLibrary;

@RegisterWorldGenerator(id = "city", displayName = "City World")
public class CityWorldGenerator extends BaseFacetedWorldGenerator {

    World world;

    private final Spawner spawner = new FixedSpawner(0, 0);

    /**
     * @param uri the uri
     */
    public CityWorldGenerator(SimpleUri uri) {
        super(uri);
    }

    @Override
    public void initialize() {

//        noiseMap = new NoiseHeightMap();
//        heightMap = HeightMaps.symmetric(noiseMap, Symmetries.alongNegativeDiagonal());

//        register(new HeightMapTerrainGenerator(heightMap));
//        register(new BoundaryGenerator(heightMap));
//        register(new CityTerrainGenerator(heightMap));
//        register(new FloraGeneratorFast(heightMap));
    }

    @Override
    public Vector3f getSpawnPosition(EntityRef entity) {
        return spawner.getSpawnPosition(getWorld(), entity);
    }

    @Override
    protected WorldBuilder createWorld() {
        int seaLevel = 2;
        WorldBuilder worldBuilder = new WorldBuilder(CoreRegistry.get(WorldGeneratorPluginLibrary.class))
                .setSeaLevel(seaLevel)
                .addProvider(new SeaLevelProvider(seaLevel))
                .addProvider(new InfiniteSurfaceHeightFacetProvider())
                .addProvider(new SurfaceHeightFacetProvider())
                .addProvider(new LakeFacetProvider())
                .addProvider(new SimpleBiomeProvider())
                .addProvider(new SettlementFacetProvider())
                .addProvider(new DefaultFloraProvider())
                .addProvider(new EnsureSpawnableChunkZeroProvider())
                .addRasterizer(new SolidRasterizer())
                .addPlugins()
                .addRasterizer(new FloraRasterizer());
        return worldBuilder;
    }

}
