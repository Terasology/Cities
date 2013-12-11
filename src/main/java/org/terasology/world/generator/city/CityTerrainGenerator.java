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

import org.terasology.math.Vector2i;
import org.terasology.math.Vector3i;
import org.terasology.world.WorldBiomeProvider;
import org.terasology.world.block.Block;
import org.terasology.world.chunks.Chunk;
import org.terasology.world.generator.FirstPassGenerator;
import org.terasology.world.generator.city.model.Sector;
import org.terasology.world.generator.city.model.Sectors;

import com.google.common.base.Function;
import com.google.common.base.Functions;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class CityTerrainGenerator implements FirstPassGenerator {

    private WorldBiomeProvider worldBiomeProvider;
    private WorldFacade facade;
    private BlockTypeFunction blockType = new BlockTypeFunction();
    private Function<? super Vector2i, Integer> heightMap = Functions.constant(50); 

    @Override
    public void setWorldSeed(String worldSeed) {
        facade = new WorldFacade(worldSeed);
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

    @Override
    public void generateChunk(Chunk chunk) {
        if (facade == null) {
            throw new IllegalStateException("seed has not been set");
        }
        
        writeChunk(chunk);
    }

    private void writeChunk(Chunk chunk) {
        Vector3i chunkPos = chunk.getBlockWorldPos(new Vector3i(0, 0, 0));
        
        int wx = chunkPos.x;
        int wz = chunkPos.z;
        int sx = (int) Math.floor((double) wx / Sector.SIZE);
        int sz = (int) Math.floor((double) wz / Sector.SIZE);
        int chunkSizeX = chunk.getChunkSizeX();
        int chunkSizeZ = chunk.getChunkSizeZ();

        Sector sector = Sectors.getSector(new Vector2i(sx, sz));
        
        Rectangle2D chunkRect = new Rectangle2D.Double(wx, wz, chunkSizeX, chunkSizeZ);

        Shape roadArea = facade.getRoadArea(sector);
        
        if (!roadArea.intersects(chunkRect)) {
            return;
        }
        
        for (int z = 0; z < chunkSizeZ; z++) {
            for (int x = 0; x < chunkSizeX; x++) {
            
                if (roadArea.contains(wx + x, wz + z)) {
                    int y = heightMap.apply(new Vector2i(wx + x, wz + z));
                    Vector3i pos = new Vector3i(x, y, z);
                    Block block = blockType.apply(BlockTypes.ROAD_SURFACE);
                    chunk.setBlock(pos, block);
                }
            }
        }
    }
}
