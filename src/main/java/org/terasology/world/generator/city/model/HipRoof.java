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

package org.terasology.world.generator.city.model;

import java.awt.Rectangle;

/**
 * A hip roof
 * @author Martin Steiger
 */
public class HipRoof implements Roof {

    private final Rectangle rc;
	private final int baseHeight;
    private final int maxHeight;
    private final int pitch;
    
    /**
     * @param rc the roof area
     * @param baseHeight the base height of the roof
     * @param maxHeight the maximum height of the roof
     * @param pitch the pitch
     */
    public HipRoof(Rectangle rc, int baseHeight, int maxHeight, int pitch) {
    	this.rc = rc;
    	this.baseHeight = baseHeight;
        this.maxHeight = maxHeight;
        this.pitch = pitch;
    }

    /**
     * @return the maximum height of the roof
     */
    public int getMaxHeight() {
        return this.maxHeight;
    }
    
    /**
	 * @return the roof area
	 */
	public Rectangle getArea()
	{
		return this.rc;
	}

	/**
     * @return the slope
     */
    public int getPitch() {
        return this.pitch;
    }

	/**
	 * @return the base height of the roof
	 */
	public int getBaseHeight()
	{
		return baseHeight;
	}    
    
}
