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

/**
 * A rectangular window in a wall
 * @author Martin Steiger
 */
public class SimpleWindow {

    private Orientation orientation;
    private Rectangle rect;
    private int baseHeight;
    private int topHeight;
    
    /**
     * @param orientation the orientation
     * @param rect the layout shape rect
     * @param baseHeight the height at the bottom
     * @param topHeight the height at the top
     */
    public SimpleWindow(Orientation orientation, Rectangle rect, int baseHeight, int topHeight) {
        this.orientation = orientation;
        this.rect = rect;
        this.baseHeight = baseHeight;
        this.topHeight = topHeight;
    }

    /**
     * @return the orientation
     */
    public Orientation getOrientation() {
        return this.orientation;
    }

    /**
     * @return the rect
     */
    public Rectangle getRect() {
        return this.rect;
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
    }}
