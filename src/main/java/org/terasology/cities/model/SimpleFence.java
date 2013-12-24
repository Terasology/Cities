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

import org.terasology.cities.common.Orientation;
import org.terasology.math.Vector2i;

/**
 * A rectangular fence
 * @author Martin Steiger
 */
public class SimpleFence {

    private final Rectangle rect;
    private final Vector2i gate;
    private final Orientation gateOrient;
    
    /**
     * @param rect the fence outline area
     * @param gateOrient the gate's orientation
     * @param gate the gate position
     */
    public SimpleFence(Rectangle rect, Orientation gateOrient, Vector2i gate) {
        
        this.rect = rect;
        this.gateOrient = gateOrient;
        this.gate = gate;
    }

    /**
     * @return the rect
     */
    public Rectangle getRect() {
        return this.rect;
    }
    
    /**
     * @return the gate orientation
     */
    public Orientation getGateOrientation() {
        return this.gateOrient;
    }

    /**
     * @return the gate
     */
    public Vector2i getGate() {
        return this.gate;
    }
}
