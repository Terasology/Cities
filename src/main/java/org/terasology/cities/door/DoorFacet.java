// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.cities.door;

import org.terasology.engine.math.Region3i;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.facets.base.BaseFacet2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A registry for doors.
 */
public class DoorFacet extends BaseFacet2D {

    private final Collection<Door> doors = new ArrayList<>();

    public DoorFacet(Region3i targetRegion, Border3D border) {
        super(targetRegion, border);
    }

    public void addDoor(Door bldg) {
        doors.add(bldg);
    }

    public Collection<Door> getDoors() {
        return Collections.unmodifiableCollection(doors);
    }
}
