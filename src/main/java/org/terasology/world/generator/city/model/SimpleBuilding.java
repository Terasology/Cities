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

import com.google.common.collect.Sets;

/**
 * Defines a building in the most common sense
 * @author Martin Steiger
 */
public class SimpleBuilding extends Building<Rectangle> {

    private Rectangle door;
    private int doorHeight;
    
    private Roof roof;
    
    private final Set<Rectangle> windows = Sets.newHashSet();

    /**
     * @param layout the building layout
     * @param roof the roof definition
     * @param door the door area in one of the wall
     * @param baseHeight the height of the floor level
     * @param wallHeight the building height above the floor level
     * @param doorHeight the height of the door
     */
    public SimpleBuilding(Rectangle layout, Roof roof, int baseHeight, int wallHeight, Rectangle door, int doorHeight) {
        super(layout, baseHeight, wallHeight);
        this.roof = roof;
        this.door = door;
        this.doorHeight = doorHeight;
    }
    

    /**
     * @return the door area in one of the wall
     */
    public Rectangle getDoor() {
        return door;
    }
    
    /**
     * @return the height of the door
     */
    public int getDoorHeight() {
        return this.doorHeight;
    }

    /**
     * @return the roof the roof definition
     */
    public Roof getRoof() {
        return this.roof;
    }

    /**
     * @return the window areas
     */
    public Set<Rectangle> getWindows() {
        return Collections.unmodifiableSet(windows);
    }
}
