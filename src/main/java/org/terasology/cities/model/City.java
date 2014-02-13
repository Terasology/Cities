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

import java.util.Objects;
import java.util.Set;

import javax.vecmath.Point2i;
import javax.vecmath.Vector2d;

import com.google.common.collect.Sets;

/**
 * Provides information on a city
 * @author Martin Steiger
 */
public class City implements NamedArea {

    private final Point2i coords;
    private final Set<Lot> lots = Sets.newHashSet();
    private String name;
    private int radius;

    /**
     * @param name the name of the city
     * @param radius the city radius in blocks
     * @param coords the world coordinate in blocks
     */
    public City(String name, Point2i coords, int radius) {
        this.radius = radius;
        this.name = name;
        this.coords = new Point2i(coords);
    }

    /**
     * @return the city center in block world coordinates
     */
    public Point2i getPos() {
        return coords;
    }
    
    /**
     * @return the city center in sectors
     */
    public Sector getSector() {
        return Sectors.getSectorForBlock(coords.x, coords.y);
    }

    /**
     * @return the diameter of the city (in blocks)
     */
    public double getDiameter() {
        return this.radius * 2;
    }

    /**
     * @return the radius of the city (in blocks)
     */
    public double getRadius() {
        return this.radius;
    }
    
    /**
     * @return all lots that are part of the city
     */
    public Set<Lot> getLots() {
        return lots;
    }

    /**
     * @param lot the lot to add
     */
    public void add(Lot lot) {
        this.lots.add(lot);
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
        return getName();
    }

    /**
     * @return the name of the city
     */
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public boolean contains(Vector2d pos) {
        double cx = coords.x - pos.x;
        double cz = coords.y - pos.y;
        
        return cx * cx + cz * cz < radius * radius;
    }
}
