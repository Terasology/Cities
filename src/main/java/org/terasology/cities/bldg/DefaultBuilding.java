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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.terasology.commonworld.Orientation;

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

    void addPart(BuildingPart part) {
        parts.add(part);
    }

    @Override
    public Collection<BuildingPart> getParts() {
        return Collections.unmodifiableCollection(parts);
    }


}
