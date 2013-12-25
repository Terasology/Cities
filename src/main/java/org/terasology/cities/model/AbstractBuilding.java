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

import java.awt.Shape;
import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * Defines a building in the most common sense
 * @author Martin Steiger
 */
public class AbstractBuilding implements Building {
    
    private final Set<Window> windows = Sets.newHashSet();
    private final Shape layout;
    private final int wallHeight;
    private final int baseHeight;
    private final Roof roof;

    /**
     * @param layout the building layout
     * @param roof the roof definition
     * @param baseHeight the height of the floor level
     * @param wallHeight the building height above the floor level
     */
    public AbstractBuilding(Shape layout, Roof roof, int baseHeight, int wallHeight) {
        this.layout = layout;
        this.roof = roof;
        this.baseHeight = baseHeight;
        this.wallHeight = wallHeight;
    }

    /**
     * @return the building layout
     */
    @Override
    public Shape getLayout() {
        return this.layout;
    }

    /**
     * @return the roof the roof definition
     */
    public Roof getRoof() {
        return this.roof;
    }

    /**
     * @return the building height
     */
    public int getWallHeight() {
        return this.wallHeight;
    }

    /**
     * @return the base height
     */
    public int getBaseHeight() {
        return baseHeight;
    }
    
    /**
     * @param window the window to add
     */
    public void addWindow(Window window) {
        windows.add(window);
    }

    /**
     * @return the window areas
     */
    public Set<Window> getWindows() {
        return Collections.unmodifiableSet(windows);
    }
}
