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

import org.terasology.world.chunks.Chunk;

/**
 * Converts T elements into blocks
 * @param <T> the element type 
 * @author Martin Steiger
 */
public interface Rasterizer<T>
{
	/**
	 * @param chunk the chunk that is filled
	 * @param brush the brush
	 * @param element the object that is converted into blocks
	 */
	public void raster(Chunk chunk, Brush brush, T element);
}
