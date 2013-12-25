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

package org.terasology.cities.model;

import java.awt.Rectangle;

/**
 * Defines a rectangular building part
 * @author Martin Steiger
 */
public class SimpleBuildingPart implements BuildingPart {

    private final Rectangle layout;
    private final int baseHeight;
    private final int topHeight;
    private final Roof roof;
    
    /**
     * @param layout the floor layout
     * @param baseHeight the base height
     * @param topHeight the top height (==roof base height)
     * @param roof the roof type
     */
    public SimpleBuildingPart(Rectangle layout, int baseHeight, int topHeight, Roof roof) {
        this.layout = layout;
        this.baseHeight = baseHeight;
        this.topHeight = topHeight;
        this.roof = roof;
    }

    /**
     * @return the layout
     */
    @Override
    public Rectangle getLayout() {
        return this.layout;
    }
    
    /**
     * @return the baseHeight
     */
    public int getBaseHeight() {
        return this.baseHeight;
    }
    
    /**
     * @return the topHeight
     */
    public int getTopHeight() {
        return this.topHeight;
    }
    
    /**
     * @return the roof
     */
    public Roof getRoof() {
        return this.roof;
    }
}
