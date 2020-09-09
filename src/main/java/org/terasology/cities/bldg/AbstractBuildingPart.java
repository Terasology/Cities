// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.cities.bldg;

import com.google.common.collect.Sets;
import org.terasology.cities.deco.Decoration;
import org.terasology.cities.door.Door;
import org.terasology.cities.model.roof.Roof;
import org.terasology.cities.window.Window;
import org.terasology.math.geom.Shape;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public abstract class AbstractBuildingPart implements BuildingPart {

    private final Set<Window> windows = Sets.newHashSet();
    private final Set<Door> doors = Sets.newHashSet();
    private final Set<Decoration> decorations = new HashSet<>();
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
    public AbstractBuildingPart(Shape layout, Roof roof, int baseHeight, int wallHeight) {
        this.layout = layout;
        this.roof = roof;
        this.baseHeight = baseHeight;
        this.wallHeight = wallHeight;
    }

    @Override
    public Shape getShape() {
        return this.layout;
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
