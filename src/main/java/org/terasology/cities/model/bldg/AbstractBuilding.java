/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.cities.model.bldg;

import java.awt.Shape;
import java.util.Collections;
import java.util.Set;

import org.terasology.cities.model.roof.Roof;

import com.google.common.collect.Sets;

/**
 * Defines a building in the most common sense
 */
public abstract class AbstractBuilding implements Building {
    
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
