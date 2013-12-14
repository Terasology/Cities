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
import java.util.Collections;
import java.util.Set;

/**
 * Defines a building in the most common sense
 * @author Martin Steiger
 */
public class SimpleBuilding extends Building<Rectangle> {

    private Rectangle door;
    private Set<Rectangle> windows;

    /**
     * @param layout the building layout
     * @param door the door area in one of the wall
     * @param baseHeight the height of the floor level
     * @param wallHeight the building height above the floor level
     */
    public SimpleBuilding(Rectangle layout, int baseHeight, int wallHeight, Rectangle door) {
        super(layout, baseHeight, wallHeight);
        this.door = door;
    }
    

    /**
     * @return the door area in one of the wall
     */
    public Rectangle getDoor() {
        return door;
    }
    
    /**
     * @return the window areas
     */
    public Set<Rectangle> getWindows() {
        return Collections.unmodifiableSet(windows);
    }
}
