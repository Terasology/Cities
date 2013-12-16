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

package nexus.model.raster;


import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.chunks.Chunk;

import com.google.common.base.Function;

/**
 * The actual implementation of {@link Brush}
 * @author Martin Steiger
 */
public class TeraBrush extends AbstractBrush
{
	private final Function<String, Block> blockType;

	/**
	 * @param blockType a mapping block type -> block
	 */
	public TeraBrush(Function<String, Block> blockType)
	{
		this.blockType = blockType;
	}
	
	@Override
	public boolean isAir(Chunk chunk, int x, int y, int z) {
        int lx = x - chunk.getBlockWorldPosX(0);
        int ly = y - chunk.getBlockWorldPosY(0);
        int lz = z - chunk.getBlockWorldPosZ(0);

        if (lx < 0 || lx >= chunk.getChunkSizeX()) {
            lx = 0;
        }

        if (ly < 0 || ly >= chunk.getChunkSizeY())
            ly = 0;

        if (lz < 0 || lz >= chunk.getChunkSizeZ())
            lz = 0;

        return chunk.getBlock(lx, ly, lz) == BlockManager.getAir();
	}
	
	@Override
	public void setBlock(Chunk chunk, int x, int y, int z, String type) {
		int lx = x - chunk.getBlockWorldPosX(0);
		int ly = y - chunk.getBlockWorldPosY(0);
		int lz = z - chunk.getBlockWorldPosZ(0);

        if (lx < 0 || lx >= chunk.getChunkSizeX())
            lx = 0;

        if (ly < 0 || ly >= chunk.getChunkSizeY())
            ly = 0;

        if (lz < 0 || lz >= chunk.getChunkSizeZ())
            lz = 0;

        Block block = blockType.apply(type);

		chunk.setBlock(lx, ly, lz, block);
    }
}
