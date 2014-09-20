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

import org.terasology.commonworld.Sector;
import org.terasology.math.Vector2i;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generator.ChunkGenerationPass;

import com.google.common.base.Function;

/**
 * Draws chunk and sector borders
 *
 * @author Martin Steiger
 */
public class BoundaryGenerator implements ChunkGenerationPass {

    private Function<Vector2i, Integer> heightMap;

    private final BlockManager blockManager = CoreRegistry.get(BlockManager.class);
    private final Block chunkBorder = blockManager.getBlock("Cities:black");
    private final Block sectorBorder = blockManager.getBlock("Cities:pink");

    /**
     * @param heightMap the height map to use
     */
    public BoundaryGenerator(Function<Vector2i, Integer> heightMap) {
        this.heightMap = heightMap;
    }

    @Override
    public void setWorldSeed(String seed) {
        // ignore
    }

    @Override
    public Map<String, String> getInitParameters() {
        return Collections.emptyMap();
    }

    @Override
    public void setInitParameters(Map<String, String> initParameters) {
        // ignore
    }

    @Override
    public void generateChunk(CoreChunk chunk) {

        int wx = chunk.chunkToWorldPositionX(0);
        int wz = chunk.chunkToWorldPositionZ(0);

        for (int z = 0; z < chunk.getChunkSizeZ(); z++) {
            for (int x = 0; x < chunk.getChunkSizeX(); x++) {
                Block block = null;
                int y = heightMap.apply(new Vector2i(wx + x, wz + z));

                if (x == 0 || z == 0) {
                    block = chunkBorder;
                }

                if (x == 0 && (wx % Sector.SIZE == 0)) {
                    block = sectorBorder;
                }

                if (z == 0 && (wz % Sector.SIZE == 0)) {
                    block = sectorBorder;
                }

                if (block != null) {
                    chunk.setBlock(x, y, z, block);
                }
            }
        }

    }

}
