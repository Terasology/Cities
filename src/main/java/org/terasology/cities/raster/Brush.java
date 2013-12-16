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

package org.terasology.cities.raster;

import java.awt.Rectangle;

import org.terasology.math.Vector3i;
import org.terasology.world.chunks.Chunk;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public interface Brush
{
	/**
	 * @param chunk the chunk to write to
	 * @param x x in world coords
	 * @param y y in world coords
	 * @param z z in world coords
	 * @return true if the block is air
	 */
	boolean isAir(Chunk chunk, int x, int y, int z);

	/**
	 * @param chunk the chunk to write to
	 * @param x x in world coords
	 * @param y y in world coords
	 * @param z z in world coords
	 * @param type the block type
	 */
	void setBlock(Chunk chunk, int x, int y, int z, String type);

	/**
	 * Removes all blocks above a defined height level
	 * @param chunk the chunk
	 * @param rc the area to clear
	 * @param y1 the base height to start
	 */
	void clearAbove(Chunk chunk, Rectangle rc, int y1);

	/**
	 * Fills all air block below a given height level with the specified block type
	 * @param chunk the chunk
	 * @param fullRc the area to clear
	 * @param y1 the base height to start
	 * @param type the block type that is used for filling
	 */
	void fillAirBelow(Chunk chunk, Rectangle fullRc, int y1, String type);

	/**
	 * Fill a cuboid with a specified block
	 * @param chunk the chunk
	 * @param from the starting coordinates (inclusive)
	 * @param to the end coordinates (exclusive)
	 * @param type the block type to use for filling
	 */
	void fill(Chunk chunk, Vector3i from, Vector3i to, String type);

	public void createWallX(Chunk chunk, int x1, int x2, int z, int baseHeight, int height, String type);
	
	public void createWallZ(Chunk chunk, int z1, int z2, int x, int baseHeight, int height, String type);
	
	public void fill(Chunk chunk, Rectangle rc, int y1, int y2, String type);
	
	Rectangle getIntersectionArea(Chunk chunk, int x1, int z1, int x2, int z2);

	Rectangle getIntersectionArea(Chunk chunk, Rectangle fullRc);

	Rectangle getIntersectionArea(Chunk chunk, Vector3i from, Vector3i to);
}
