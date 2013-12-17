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

package org.terasology.cities.terrain;

import org.terasology.math.Vector2i;

import com.google.common.base.Function;

/**
 * Definition of a height map
 * @author Martin Steiger
 */
public interface HeightMap extends Function<Vector2i, Integer> {

    /**
     * @param x the x world coord
     * @param z the z world coord
     * @return the height
     */
    int apply(int x, int z);

}
