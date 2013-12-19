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
 * A flat battlement roof with merlons
 * @author Martin Steiger
 */
public class BattlementRoof extends FlatRoof {

    /**
     * @param rc the roof shape
     * @param baseHeight the base height of the roof
     * @param merlonHeight the height of the border
     */
    public BattlementRoof(Rectangle rc, int baseHeight, int merlonHeight) {
        super(rc, baseHeight, merlonHeight);
    }

    /**
     * @param lx x in local (roof area) coordinates
     * @param lz z in local (roof area) coordinates
     * @return the borderHeight
     */
    @Override
    public int getBorderHeight(int lx, int lz) {
        if (lx % 2 == 1) {
            return 0;
        }
        
        if (lz % 2 == 1) {
            return 0;
        }
        
        return super.getBorderHeight(lx, lz);
    }
}
