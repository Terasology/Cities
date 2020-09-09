// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.cities.bldg;

import org.terasology.commonworld.Orientation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A building - composed of {@link BuildingPart} instances.
 */
public class DefaultBuilding implements Building {

    private final Orientation orient;
    private final Collection<BuildingPart> parts = new ArrayList<>();

    /**
     * @param orient the building's orientation
     */
    public DefaultBuilding(Orientation orient) {
        this.orient = orient;
    }

    /**
     * @return the building's orientation
     */
    public Orientation getOrientation() {
        return this.orient;
    }

    public void addPart(BuildingPart part) {
        parts.add(part);
    }

    @Override
    public Collection<BuildingPart> getParts() {
        return Collections.unmodifiableCollection(parts);
    }


}
