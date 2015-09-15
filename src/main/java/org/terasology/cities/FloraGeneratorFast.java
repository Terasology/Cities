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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.core.world.CoreBiome;
import org.terasology.registry.CoreRegistry;
import org.terasology.utilities.random.FastRandom;
import org.terasology.utilities.random.Random;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generator.ChunkGenerationPass;

import com.google.common.collect.Lists;

/**
 * Generates flowers and high grass. It's fast, because it
 * uses a height map to find the terrain layer.
 */
public class FloraGeneratorFast implements ChunkGenerationPass {

    private static final String[] FLOWER_BLOCKS = new String[]{
            "core:YellowFlower", "core:RedFlower",
            "core:BrownShroom", "core:BigBrownShroom", "core:RedShroom",
            "core:RedClover", "core:Lavender", "core:Iris", "core:GlowbellBloom", "core:Glowbell",
            "core:DeadBush", "core:Dandelion", "core:Tulip",
            "core:Cotton1", "core:Cotton2", "core:Cotton3", "core:Cotton4", "core:Cotton5", "core:Cotton6"};

    private BlockManager blockManager = CoreRegistry.get(BlockManager.class);
    private Block grassBlock;
    private Block tallGrass1;
    private Block tallGrass2;
    private Block tallGrass3;
    private List<Block> flowers = Lists.newArrayList();

    private WorldGenerationConfig config = new WorldGenerationConfig();

    private HeightMap heightMap;

    private String worldSeed;

    /**
     * @param heightMap the height map to use
     */
    public FloraGeneratorFast(HeightMap heightMap) {
        grassBlock = blockManager.getBlock("core:Grass");
        tallGrass1 = blockManager.getBlock("core:TallGrass1");
        tallGrass2 = blockManager.getBlock("core:TallGrass2");
        tallGrass3 = blockManager.getBlock("core:TallGrass3");
        for (String blockUrn : FLOWER_BLOCKS) {
            flowers.add(blockManager.getBlock(blockUrn));
        }

        this.heightMap = heightMap;
    }

    public void setWorldSeed(String seed) {
        this.worldSeed = seed;
    }

    @Override
    public void generateChunk(CoreChunk chunk) {
        int seed = Objects.hash(worldSeed.hashCode(), chunk.getPosition());

        Random random = new FastRandom(seed);
        for (int x = 0; x < chunk.getChunkSizeX(); x++) {
            for (int z = 0; z < chunk.getChunkSizeZ(); z++) {
                int y = heightMap.apply(x, z);
                Block targetBlock = chunk.getBlock(x, y, z);

                // check for grass and air above
                if (targetBlock.equals(grassBlock)) {
                    Block blockOnTop = chunk.getBlock(x, y + 1, z);

                    if (blockOnTop.equals(blockManager.getBlock(BlockManager.AIR_ID))) {
                        generateGrassAndFlowers(chunk, x, y, z, random);
                    }
                }
            }
        }
    }

    /**
     * Generates grass or a flower on the given chunk.
     *
     * @param c      The chunk
     * @param x      Position on the x-axis
     * @param y      Position on the y-axis
     * @param z      Position on the z-axis
     * @param random the RNG
     */
    private void generateGrassAndFlowers(CoreChunk c, int x, int y, int z, Random random) {

        if (random.nextFloat() < config.getGrassDensity(CoreBiome.PLAINS)) {
            /*
             * Generate tall grass.
             */
            double rand = random.nextGaussian();

            if (rand > -0.4 && rand < 0.4) {
                c.setBlock(x, y + 1, z, tallGrass1);
            } else if (rand > -0.6 && rand < 0.6) {
                c.setBlock(x, y + 1, z, tallGrass2);
            } else {
                c.setBlock(x, y + 1, z, tallGrass3);
            }

            /*
             * Generate flowers.
             */
            if (random.nextGaussian() < -2) {
                c.setBlock(x, y + 1, z, random.nextItem(flowers));
            }
        }
    }

    @Override
    public Map<String, String> getInitParameters() {
        return Collections.emptyMap();
    }

    @Override
    public void setInitParameters(final Map<String, String> initParameters) {
        // ignore
    }
}
