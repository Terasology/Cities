// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.cities.bldg;

import org.terasology.cities.deco.Decoration;
import org.terasology.cities.door.Door;
import org.terasology.cities.model.roof.Roof;
import org.terasology.cities.window.Window;
import org.terasology.math.geom.Shape;

import java.util.Set;

/**
 * Defines a part of a building. This is similar to an entire building.
 */
public interface BuildingPart {

    /**
     * @return the building layout
     */
    Shape getShape();

    Roof getRoof();

    int getWallHeight();

    int getBaseHeight();

    /**
     * @return baseHeight + wallHeight;
     */
    default int getTopHeight() {
        return getBaseHeight() + getWallHeight();
    }

    Set<Window> getWindows();

    Set<Door> getDoors();

    Set<Decoration> getDecorations();
}
