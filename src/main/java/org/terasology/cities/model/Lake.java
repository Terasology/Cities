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

package org.terasology.cities.model;

import java.util.Collections;
import java.util.Set;

import org.terasology.commonworld.contour.Contour;
import org.terasology.math.geom.BaseVector2d;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

/**
 * Defines a lake
 */
public class Lake implements NamedArea {

    private final Contour contour;
    private final Set<Contour> islands = Sets.newHashSet();
    private final String name;

    /**
     * @param contour the contour of the lake (never <code>null</code>)
     * @param name the name of the lake
     */
    public Lake(Contour contour, String name) {
        Preconditions.checkArgument(contour != null, "Contour must not be null");

        this.contour = contour;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Islands are considered part of the lake
     */
    @Override
    public boolean contains(BaseVector2d pos) {
        return contour.isInside(pos.getX(), pos.getY());
    }

    /**
     * @return the contour
     */
    public Contour getContour() {
        return this.contour;
    }

    /**
     * @param cont the island contour
     */
    public void addIsland(Contour cont) {
        islands.add(cont);
    }

    /**
     * @return the set of island contours
     */
    public Set<Contour> getIslandContours() {
        return Collections.unmodifiableSet(islands);
    }

    @Override
    public String toString() {
        return "Lake " + name + " [" + islands.size() + " islands]";
    }
}


