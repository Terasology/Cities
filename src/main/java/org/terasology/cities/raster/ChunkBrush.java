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

import org.terasology.world.block.Block;
import org.terasology.world.chunks.Chunk;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

/**
 * Converts model elements into blocks of of a chunk
 * @author Martin Steiger
 */
public class ChunkBrush extends Brush {
    private final Chunk chunk;
    private Function<String, Block> blockType;
    
    /**
     * @param chunk the chunk to work on
     * @param blockType a mapping String type -> block
     */
    public ChunkBrush(Chunk chunk, Function<String, Block> blockType) {
        this.blockType = blockType;
        this.chunk = chunk;
    }

    @Override
    public Rectangle getAffectedArea() {
        int wx = chunk.getBlockWorldPosX(0);
        int wz = chunk.getBlockWorldPosZ(0);
        return new Rectangle(wx, wz, chunk.getChunkSizeX(), chunk.getChunkSizeZ());
    }

    @Override
    public int getMaxHeight() {
        return chunk.getBlockWorldPosY(0) + chunk.getChunkSizeY();
    }
    
    @Override
    public int getMinHeight() {
        return chunk.getBlockWorldPosY(0);
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
        
        int wx = chunk.getBlockWorldPosX(0);
        int wy = chunk.getBlockWorldPosY(0);
        int wz = chunk.getBlockWorldPosZ(0);

        int lx = x - wx;
        int ly = y - wy;
        int lz = z - wz;

        // TODO: remove
        final boolean debugging = true;
        if (debugging) {
            Preconditions.checkArgument(lx >= 0 && lx < chunk.getChunkSizeX(), "X value of %s not in rage [%s..%s]", x, wx, wx + chunk.getChunkSizeX());
            Preconditions.checkArgument(ly >= 0 && ly < chunk.getChunkSizeY(), "Y value of %s not in rage [%s..%s]", y, wy, wy + chunk.getChunkSizeY());
            Preconditions.checkArgument(lz >= 0 && lz < chunk.getChunkSizeZ(), "Z value of %s not in rage [%s..%s]", z, wz, wz + chunk.getChunkSizeZ());
        }
            

        chunk.setBlock(lx, ly, lz, block);
        
    }
}
