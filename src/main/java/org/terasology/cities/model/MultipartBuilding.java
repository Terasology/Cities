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
import java.awt.geom.Path2D;
import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * A building that consists of several, clearly separable parts.
 * @author Martin Steiger
 */
public abstract class MultipartBuilding implements Building {

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

    
    
}
