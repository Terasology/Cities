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

import java.util.Collections;
import java.util.Map;

import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.math.Vector2i;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generator.ChunkGenerationPass;
import org.terasology.world.liquid.LiquidData;
import org.terasology.world.liquid.LiquidType;

import com.google.common.base.Function;

/**
 * Generates terrain based on a height map function
 *
 * @author Martin Steiger
 */
public class HeightMapTerrainGenerator implements ChunkGenerationPass {

    private Function<Vector2i, Integer> heightMap;

    private final BlockManager blockManager = CoreRegistry.get(BlockManager.class);

    private final Block air = BlockManager.getAir();
    private final Block mantle = blockManager.getBlock("core:MantleStone");
    private final Block stone = blockManager.getBlock("core:Stone");
    private final Block sand = blockManager.getBlock("core:Sand");
    private final Block grass = blockManager.getBlock("core:Grass");
    private final Block snow = blockManager.getBlock("core:Snow");
    private final Block dirt = blockManager.getBlock("core:Dirt");
    private final Block water = blockManager.getBlock("core:water");

    private final CityTerrainComponent config;

    /**
     * @param heightMap the height map to use
     */
    public HeightMapTerrainGenerator(HeightMap heightMap) {
        this.heightMap = heightMap;

        config = WorldFacade.getWorldEntity().getComponent(CityTerrainComponent.class);

    }

    /**
     * @return The height map
     */
    public Function<Vector2i, Integer> getHeightMap() {
        return heightMap;
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
    public void setWorldSeed(String seed) {
        // ignore
    }

    /**
     * The generated terrain looks like this
     * <pre>
     * ---------------------------
     *  AIR
     * ---------------------------
     *  GRASS       surfaceHeight && < snowLine
     *  SNOW        surfaceHeight && >= snowLine
     *  SAND        surfaceHeight && seaLevel + 1
     * ---------------------------
     *  DIRT        < surfaceHeight && < snowLine
     *  STONE       < surfaceHeight && >= snowLine
     * ---------------------------
     *  WATER       <= seaLevel
     * ---------------------------
     *  MANTLE      0
     * ---------------------------
     * </pre>
     */
    @Override
    public void generateChunk(CoreChunk chunk) {
        if (chunk.getPosition().y != 0) {
            return;
        }

        int seaLevel = config.getSeaLevel();
        int snowLine = config.getSnowLine();

        for (int x = 0; x < chunk.getChunkSizeX(); x++) {
            for (int z = 0; z < chunk.getChunkSizeZ(); z++) {
                int wx = chunk.chunkToWorldPositionX(x);
                int wz = chunk.chunkToWorldPositionZ(z);

                int surfaceHeight = heightMap.apply(new Vector2i(wx, wz));

                for (int y = chunk.getChunkSizeY() - 1; y >= 0; y--) {
                    if (y == 0) {
                        // bedrock/mantle
                        chunk.setBlock(x, y, z, mantle);
                    } else if (y <= seaLevel) { // Ocean
                        chunk.setBlock(x, y, z, water);
                        chunk.setLiquid(x, y, z, new LiquidData(LiquidType.WATER, LiquidData.MAX_LIQUID_DEPTH));
                    } else if (y == seaLevel + 1 && y == surfaceHeight) {
                        chunk.setBlock(x, y, z, sand);
                    } else if (y < surfaceHeight && surfaceHeight < snowLine) {
                        chunk.setBlock(x, y, z, dirt);
                    } else if (y < surfaceHeight && surfaceHeight >= snowLine) {
                        chunk.setBlock(x, y, z, stone);
                    } else if (y == surfaceHeight && surfaceHeight < snowLine) {
                        chunk.setBlock(x, y, z, grass);
                    } else if (y == surfaceHeight && surfaceHeight >= snowLine) {
                        chunk.setBlock(x, y, z, snow);
                    } else {
                        chunk.setBlock(x, y, z, air);
                    }
                }
            }
        }
    }
}
