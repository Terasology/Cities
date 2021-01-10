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

package org.terasology.cities.bldg;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.terasology.cities.deco.Decoration;
import org.terasology.cities.door.Door;
import org.terasology.cities.model.roof.Roof;
import org.terasology.cities.window.Window;
import org.terasology.math.geom.Shape;

import com.google.common.collect.Sets;

/**
 *
 */
public abstract class AbstractBuildingPart implements BuildingPart {

    private final Set<Window> windows = Sets.newHashSet();
    private final Set<Door> doors = Sets.newHashSet();
    private final Set<Decoration> decorations = new HashSet<>();
    private final int wallHeight;
    private final int baseHeight;
    private final Roof roof;

    /**
     * @param roof the roof definition
     * @param baseHeight the height of the floor level
     * @param wallHeight the building height above the floor level
     */
    public AbstractBuildingPart(Roof roof, int baseHeight, int wallHeight) {
        this.roof = roof;
        this.baseHeight = baseHeight;
        this.wallHeight = wallHeight;
    }

    @Override
    public Roof getRoof() {
        return this.roof;
    }

    @Override
    public int getWallHeight() {
        return this.wallHeight;
    }

    @Override
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
     * @param door the door to add
     */
    public void addDoor(Door door) {
        doors.add(door);
    }

    /**
     * @param decoration the decoration to add
     */
    public void addDecoration(Decoration decoration) {
        decorations.add(decoration);
    }

    @Override
    public Set<Window> getWindows() {
        return Collections.unmodifiableSet(windows);
    }

    @Override
    public Set<Door> getDoors() {
        return Collections.unmodifiableSet(doors);
    }

    @Override
    public Set<Decoration> getDecorations() {
        return Collections.unmodifiableSet(decorations);
    }
}
