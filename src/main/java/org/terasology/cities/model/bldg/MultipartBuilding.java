/*
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.cities.model.bldg;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * A building that consists of several, clearly separable parts.
 * @author Martin Steiger
 */
public abstract class MultipartBuilding implements Building {

    private final Set<Window> windows = Sets.newHashSet();

    private final Set<BuildingPart> parts = Sets.newHashSet();
    private final Path2D layout = new Path2D.Double();

    /**
     * @param part the part to add
     */
    public void addPart(BuildingPart part) {
        // TODO: maybe verify that parts don't intersect, but are touching
        parts.add(part);
        layout.append(part.getLayout(), false);
    }
    
    /**
     * @return the combined layout of all building parts
     */
    @Override
    public Shape getLayout() {
        return this.layout;
    }

    /**
     * @return the parts
     */
    public Set<BuildingPart> getParts() {
        return Collections.unmodifiableSet(parts);
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
