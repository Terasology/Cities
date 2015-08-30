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

import java.util.Objects;

import org.terasology.commonworld.Sector;
import org.terasology.commonworld.Sectors;
import org.terasology.math.geom.ImmutableVector2i;

/**
 * Provides information on a city
  */
public class Site {

    private final ImmutableVector2i coords;
    private int radius;

    /**
     * @param radius the city radius in blocks
     * @param bx the x coord (in blocks)
     * @param bz the z coord (in blocks)
     */
    public Site(int bx, int bz, int radius) {
        this.radius = radius;
        this.coords = new ImmutableVector2i(bx, bz);
    }

    /**
     * @return the city center in block world coordinates
     */
    public ImmutableVector2i getPos() {
        return coords;
    }

    /**
     * @return the radius of the settlements in blocks
     */
    public int getRadius() {
        return radius;
    }

    /**
     * @return the city center in sectors
     */
    public Sector getSector() {
        return Sectors.getSectorForBlock(coords.getX(), coords.getY());
    }

    @Override
    public int hashCode() {
        return Objects.hash(coords, radius);
    }

    @Override
    public boolean equals(Object obj) {
        return Objects.equals(coords, radius);
    }

    @Override
    public String toString() {
        return "Site " + coords + " (" + radius + ")";
    }
}
