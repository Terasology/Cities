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
 * A roof with rectangular shape
 * @author Martin Steiger
 */
public class RectangularRoof implements Roof {
    
    private final Rectangle rc;
    private final int baseHeight;

    /**
     * @param rc the roof area
     * @param baseHeight the base height of the roof
     */
    public RectangularRoof(Rectangle rc, int baseHeight) {
        this.rc = rc;
        this.baseHeight = baseHeight;
    }

    /**
     * @return the base height of the roof
     */
    public int getBaseHeight() {
        return baseHeight;
    }
    
    /**
     * @return the roof area
     */
    @Override
    public Rectangle getArea() {
        return this.rc;
    }
}
