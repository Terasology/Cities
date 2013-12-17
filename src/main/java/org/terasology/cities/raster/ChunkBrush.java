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

package org.terasology.cities.raster;

import java.awt.Rectangle;

import org.terasology.cities.terrain.HeightMap;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.chunks.Chunk;

import com.google.common.base.Function;

/**
 * Converts model elements into blocks of of a chunk
 * @author Martin Steiger
 */
public class ChunkBrush extends Brush {
    private final Chunk chunk;
    private Function<String, Block> blockType;
    
    /**
     * @param chunk the chunk to work on
     * @param heightMap the height map
     * @param blockType a mapping String type -> block
     */
    public ChunkBrush(Chunk chunk, HeightMap heightMap, Function<String, Block> blockType) {
        super(heightMap);
        this.blockType = blockType;
        this.chunk = chunk;
    }

    @Override
    protected Rectangle getAffectedArea() {
        int wx = chunk.getBlockWorldPosX(0);
        int wz = chunk.getBlockWorldPosZ(0);
        return new Rectangle(wx, wz, chunk.getChunkSizeX(), chunk.getChunkSizeZ());
    }

    @Override
    public Rectangle getIntersectionArea(int x1, int z1, int x2, int z2) {

        // this just computes the intersection between getAffectedArea() and the given rectangle
        
        int wx = chunk.getBlockWorldPosX(0);
        int wz = chunk.getBlockWorldPosZ(0);

        int minX = Math.max(x1, wx);
        int maxX = Math.min(x2, wx + chunk.getChunkSizeX());

        int minZ = Math.max(z1, wz);
        int maxZ = Math.min(z2, wz + chunk.getChunkSizeZ());

        return new Rectangle(minX, minZ, maxX - minX, maxZ - minZ);
    }

    @Override
    public boolean isAir(int x, int y, int z) {
        int lx = x - chunk.getBlockWorldPosX(0);
        int ly = y - chunk.getBlockWorldPosY(0);
        int lz = z - chunk.getBlockWorldPosZ(0);

        return chunk.getBlock(lx, ly, lz) == BlockManager.getAir();
    }
    
    @Override
    protected int getMaxHeight() {
        return chunk.getChunkSizeY();
    }
    
    /**
     * @param x x in world coords
     * @param y y in world coords
     * @param z z in world coords
     * @param type the block type 
     */
    @Override
    public void setBlock(int x, int y, int z, String type) {
        setBlock(x, y, z, blockType.apply(type));
    }

    /**
     * @param x x in world coords
     * @param y y in world coords
     * @param z z in world coords
     * @param block the actual block  
     */
    protected void setBlock(int x, int y, int z, Block block) {
        int lx = x - chunk.getBlockWorldPosX(0);
        int ly = y - chunk.getBlockWorldPosY(0);
        int lz = z - chunk.getBlockWorldPosZ(0);

        chunk.setBlock(lx, ly, lz, block);
        
    }
}
