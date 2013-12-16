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

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.core.world.generator.chunkGenerators.BasicHMTerrainGenerator;
import org.terasology.core.world.generator.chunkGenerators.FlatTerrainGenerator;
import org.terasology.engine.CoreRegistry;
import org.terasology.math.TeraMath;
import org.terasology.math.Vector2i;
import org.terasology.math.Vector3i;
import org.terasology.utilities.procedural.SimplexNoise;
import org.terasology.world.WorldBiomeProvider;
import org.terasology.world.WorldBiomeProvider.Biome;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.BlockUri;
import org.terasology.world.chunks.Chunk;
import org.terasology.world.generator.FirstPassGenerator;
import org.terasology.world.generator.city.model.Sector;
import org.terasology.world.generator.city.model.Sectors;

import com.google.common.base.Function;
import com.google.common.base.Functions;

/**
 * Generates terrain based on a height map funciton 
 * -> unify with {@link BasicHMTerrainGenerator} and {@link FlatTerrainGenerator}
 * @author Martin Steiger
 */
public class HeightMapTerrainGenerator implements FirstPassGenerator {

	
    private WorldBiomeProvider worldBiomeProvider;

    private Function<Vector2i, Integer> heightMap;

    private final BlockManager blockManager = CoreRegistry.get(BlockManager.class);

    private final Block air = BlockManager.getAir();
    private final Block mantle = blockManager.getBlock("core:MantleStone");
    private final Block stone = blockManager.getBlock("core:Stone");
    private final Block sand = blockManager.getBlock("core:Sand");
    private final Block grass = blockManager.getBlock("core:Grass");
    private final Block snow = blockManager.getBlock("core:Snow");
    private final Block dirt = blockManager.getBlock("core:Dirt");


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
	
	/**
	 * @return The height map
	 */
	public Function<Vector2i, Integer> getHeightMap() {
		return heightMap;
	}

    @Override
    public void setWorldBiomeProvider(WorldBiomeProvider worldBiomeProvider) {
        this.worldBiomeProvider = worldBiomeProvider;
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

    /**
     * An plain copy of {@link FlatTerrainGenerator#generateChunk(Chunk)}
     */
    @Override
    public void generateChunk(Chunk chunk) {
        for (int x = 0; x < chunk.getChunkSizeX(); x++) {
            for (int z = 0; z < chunk.getChunkSizeZ(); z++) {
                int wx = chunk.getBlockWorldPosX(x);
				int wz = chunk.getBlockWorldPosZ(z);
				
				int surfaceHeight = heightMap.apply(new Vector2i(wx, wz));
				WorldBiomeProvider.Biome type = worldBiomeProvider.getBiomeAt(wx, wz);

				// debug
				type = Biome.PLAINS;
				
                for (int y = chunk.getChunkSizeY() - 1; y >= 0; y--) {
                    if (y == 0) {
                        // bedrock/mantle
                        chunk.setBlock(x, y, z, mantle);
                    } else if (y < surfaceHeight) {
                        // underground
                        switch (type) {
                            case FOREST:
                                chunk.setBlock(x, y, z, dirt);
                                break;
                            case PLAINS:
                                chunk.setBlock(x, y, z, dirt);
                                break;
                            case MOUNTAINS:
                                chunk.setBlock(x, y, z, stone);
                                break;
                            case SNOW:
                                chunk.setBlock(x, y, z, snow);
                                break;
                            case DESERT:
                                chunk.setBlock(x, y, z, sand);
                                break;
                        }
                    } else if (y == surfaceHeight) {
                        // surface
                        switch (type) {
                            case FOREST:
                                chunk.setBlock(x, y, z, dirt);
                                break;
                            case PLAINS:
                                chunk.setBlock(x, y, z, grass);
                                break;
                            case MOUNTAINS:
                                chunk.setBlock(x, y, z, stone);
                                break;
                            case SNOW:
                                chunk.setBlock(x, y, z, snow);
                                break;
                            case DESERT:
                                chunk.setBlock(x, y, z, sand);
                                break;
                        }
                    } else {
                        // air
                        chunk.setBlock(x, y, z, air);
                    }
                }
            }
        }
    }
}
