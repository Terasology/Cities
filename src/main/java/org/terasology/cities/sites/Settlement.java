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

package org.terasology.cities.sites;

import java.util.Objects;

import org.terasology.math.geom.Vector2i;

/**
 * Provides information on a settlement.
  */
public class Settlement {

    private final Vector2i coords;
    private float radius;
    private String name;

    /**
     * @param name the name of the settlement
     * @param radius the city radius in blocks
     * @param bx the x world coord (in blocks)
     * @param bz the z world coord (in blocks)
     */
    public Settlement(String name, int bx, int bz, float radius) {
        this.radius = radius;
        this.coords = new Vector2i(bx, bz);
        this.name = name;
    }

    /**
     * @return the city center in block world coordinates
     */
    public Vector2i getPos() {
        return coords;
    }

    /**
     * @return the radius of the settlements in blocks
     */
    public float getRadius() {
        return radius;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, coords, radius);
    }

    @Override
    public boolean equals(Object obj) {
        if (Settlement.class == obj.getClass()) {
            Settlement other = (Settlement) obj;
            return Objects.equals(name, other.name)
                && Objects.equals(coords, other.coords)
                && Objects.equals(radius, other.radius);
        }
        return false;
    }

    @Override
    public String toString() {
        return name + " " + coords + " (" + radius + ")";
    }
}
