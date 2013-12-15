/*
 * Copyright (C) 2012-2013 Martin Steiger
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

import java.awt.Rectangle;

import org.terasology.math.Vector3i;
import org.terasology.world.chunks.Chunk;

/**
 * Implements all methods except setBlock()
 * @author Martin Steiger
 */
public abstract class AbstractBrush implements Brush
{
	@Override
	public Rectangle getIntersectionArea(Chunk chunk, Vector3i from, Vector3i to)
	{
		int x1 = from.x;
		int x2 = to.x;
		int z1 = from.z;
		int z2 = to.z;
		
		return getIntersectionArea(chunk, x1, z1, x2, z2);
	}
		
	@Override
	public Rectangle getIntersectionArea(Chunk chunk, Rectangle fullRc) {
		int x1 = fullRc.x;
		int x2 = fullRc.x + fullRc.width;
		int z1 = fullRc.y;
		int z2 = fullRc.y + fullRc.height;
		
		return getIntersectionArea(chunk, x1, z1, x2, z2);
	}
	
	@Override
	public Rectangle getIntersectionArea(Chunk chunk, int x1, int z1, int x2, int z2) {
		
        int wx = chunk.getBlockWorldPosX(0);
        int wz = chunk.getBlockWorldPosZ(0);

		int minX = Math.max(x1, wx);
		int maxX = Math.min(x2, wx + chunk.getChunkSizeX());

		int minZ = Math.max(z1, wz);
		int maxZ = Math.min(z2, wz + chunk.getChunkSizeZ());

		return new Rectangle(minX, minZ, maxX - minX, maxZ - minZ);
	}

	
	@Override
	public void createWallX(Chunk chunk, int x1, int x2, int z, int baseHeight, int height, String type)
	{
		fill(chunk, new Vector3i(x1, baseHeight, z), new Vector3i(x2, baseHeight + height, z + 1), type);
	}
	
	@Override
	public void createWallZ(Chunk chunk, int z1, int z2, int x, int baseHeight, int height, String type)
	{
		fill(chunk, new Vector3i(x, baseHeight, z1), new Vector3i(x + 1, baseHeight + height, z2), type);
	}
	
	@Override
	public void fill(Chunk chunk, Rectangle rc, int y1, int y2, String type)
	{
		fill(chunk, new Vector3i(rc.x, y1, rc.y), new Vector3i(rc.x + rc.width, y2, rc.y + rc.height), type);		
	}
	
	@Override
	public void fill(Chunk chunk, Vector3i from, Vector3i to, String type)
	{
		Rectangle rc = getIntersectionArea(chunk, from, to);
		int y1 = from.y;
		int y2 = to.y;
		for (int z = rc.y; z < rc.y + rc.height; z++) {
			for (int x = rc.x; x < rc.x + rc.width; x++) {
				for (int y = y1; y < y2; y++) {
					setBlock(chunk, x, y, z, type);
				}
			}
		}
	}
	
	@Override
	public void fillAirBelow(Chunk chunk, Rectangle fullRc, int y1, String type)
	{
		Rectangle rc = getIntersectionArea(chunk, fullRc);

		for (int z = rc.y; z < rc.y + rc.height; z++) {
			for (int x = rc.x; x < rc.x + rc.width; x++) {
				
				// starting from the top, we go down as long as we encounter air
				for (int y = y1; y >= 0; y--) {
					if (!isAir(chunk, x, y, z))
						break;
					
					setBlock(chunk, x, y, z, type);
				}
			}
		}		
	}

	@Override
	public void clearAbove(Chunk chunk, Rectangle fullRc, int y1)
	{
		Rectangle rc = getIntersectionArea(chunk, fullRc);

		for (int z = rc.y; z < rc.y + rc.height; z++) {
			for (int x = rc.x; x < rc.x + rc.width; x++) {
				
				// starting from the bottom, we go up until we hit air
				for (int y = y1; y < chunk.getChunkSizeY(); y++) {
					if (isAir(chunk, x, y, z)) {
						break;
					}
					
					setBlock(chunk, x, y, z, null);
				}
			}
		}		
	}
}
