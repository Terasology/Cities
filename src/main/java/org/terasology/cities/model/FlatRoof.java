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
 * A flat roof with an extruded border (terrace roof)
 * @author Martin Steiger
 */
public class FlatRoof extends RectangularRoof {

    private final int borderHeight;

    /**
     * @param rc the roof shape
     * @param baseHeight the base height of the roof
     * @param borderHeight the height of the border
     */
    public FlatRoof(Rectangle rc, int baseHeight, int borderHeight) {
        super(rc, baseHeight);
        
        this.borderHeight = borderHeight;
    }

    /**
     * @return the borderHeight
     */
    public int getBorderHeight() {
        return borderHeight;
    }
}
