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

package org.terasology.cities.model;

import java.awt.Shape;
import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * A parcel where building can be placed on
 * @author Martin Steiger
 */
public class Lot {

    private final Shape shape;
    
    private final Set<Building> buildings = Sets.newHashSet();
    
    /**
     * @param shape the shape of the lot
     */
    public Lot(Shape shape) {
        this.shape = shape;
    }

    /**
     * @return the layout shape
     */
    public Shape getShape() {
        return this.shape;
    }

    /**
     * @param bldg the building to add
     */
    public void addBuilding(Building bldg) {
        buildings.add(bldg);
    }
    
    /**
     * @return an unmodifiable view on all buildings in this lot
     */
    public Set<Building> getBuildings() {
        return Collections.unmodifiableSet(buildings);
    }
}
