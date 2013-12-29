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

import static org.terasology.cities.common.Orientation.EAST;
import static org.terasology.cities.common.Orientation.NORTH;
import static org.terasology.cities.common.Orientation.SOUTH;
import static org.terasology.cities.common.Orientation.WEST;

import java.awt.Rectangle;

import org.terasology.cities.common.Orientation;

import com.google.common.base.Preconditions;

/**
 * A saddle(gable) roof
 * @author Martin Steiger
 */
public class SaddleRoof extends RectangularRoof {

    private final double pitch;
    
    private final Orientation orientation;

    /**
     * @param shape the roof shape
     * @param baseHeight the base height of the roof
     * @param pitch the roof pitch
     * @param orientation the orientation (only NORTH, WEST, SOUTH, EAST are allowed)
     */
    public SaddleRoof(Rectangle shape, int baseHeight, Orientation orientation, double pitch) {
        super(shape, baseHeight);

        Preconditions.checkArgument(pitch > 0 && pitch < 10, "pitch must be in [0..10]");

        Preconditions.checkArgument(
                orientation == WEST
             || orientation == NORTH
             || orientation == SOUTH 
             || orientation == EAST,
                "only NORTH, WEST, SOUTH, EAST are allowed");
        
        this.orientation = orientation;
        this.pitch = pitch;
    }

    /**
     * @return the pitch
     */
    public double getPitch() {
        return pitch;
    }

    /**
     * @return the orientation
     */
    public Orientation getOrientation() {
        return orientation;
    }

}
