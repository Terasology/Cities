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

package org.terasology.world.generator.city;

import java.util.Collections;
import java.util.Map;

import org.terasology.engine.CoreRegistry;
import org.terasology.math.Vector2i;
import org.terasology.utilities.procedural.SimplexNoise;
import org.terasology.world.WorldBiomeProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.chunks.Chunk;
import org.terasology.world.generator.FirstPassGenerator;
import org.terasology.world.generator.city.model.Sector;

import com.google.common.base.Function;

/**
 * Draws chunk and sector borders
 * @author Martin Steiger
 */
public class BoundaryGenerator implements FirstPassGenerator
{
	private Function<Vector2i, Integer> heightMap;

	private final BlockManager blockManager = CoreRegistry.get(BlockManager.class);
    private final Block chunkBorder = blockManager.getBlock("Cities:Chunk");
    private final Block sectorBorder = blockManager.getBlock("Cities:Sector");

    @Override
    public void setWorldBiomeProvider(WorldBiomeProvider biomeProvider) {
        // ignore
    }

   @Override
    public void setWorldSeed(String seed) {
       if (seed == null)
           return;
       
        final SimplexNoise terrainHeight = new SimplexNoise(seed.hashCode());

        heightMap = new Function<Vector2i, Integer>() {

            @Override
            public Integer apply(Vector2i pos) {
                double noise = terrainHeight.noise(pos.x / 155d, pos.y / 155d);
                return (int) Math.min(12, Math.max(1, (5 + noise * 15d)));
            }

        };
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
    public void generateChunk(Chunk chunk) {
        
        int wx = chunk.getBlockWorldPosX(0);
        int wz = chunk.getBlockWorldPosZ(0);

        for (int z = 0; z < chunk.getChunkSizeZ(); z++) {
            for (int x = 0; x < chunk.getChunkSizeX(); x++) {
                Block block = null;
                int y = heightMap.apply(new Vector2i(wx + x, wz + z));

                if (x == 0 || z == 0)
                    block = chunkBorder;

                if (x == 0 && (wx % Sector.SIZE == 0))
                    block = sectorBorder;
                if (z == 0 && (wz % Sector.SIZE == 0))
                    block = sectorBorder;

                if (block != null) {
                    chunk.setBlock(x, y, z, block);
                }
            }
        }

    }

}
