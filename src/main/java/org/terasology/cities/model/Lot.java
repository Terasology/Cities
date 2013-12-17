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
