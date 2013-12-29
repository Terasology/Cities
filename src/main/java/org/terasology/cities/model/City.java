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

import java.util.Objects;
import java.util.Set;

import javax.vecmath.Point2i;

import org.terasology.cities.common.Base58;

import com.google.common.collect.Sets;

/**
 * Provides information on a city
 * @author Martin Steiger
 */
public class City {

    private final Point2i coords;
    private final Set<Lot> lots = Sets.newHashSet();
    private double diameter;

    /**
     * @param diameter the city diameter in blocks
     * @param bx the x coord (in blocks)
     * @param bz the z coord (in blocks)
     */
    public City(double diameter, int bx, int bz) {
        this.diameter = diameter;
        
        this.coords = new Point2i(bx, bz);
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
        return this.diameter;
    }

    /**
     * @return all lots that are part of the city
     */
    public Set<Lot> getLots() {
        return lots;
    }

    @Override
    public int hashCode() {
        return Objects.hash(coords);
    }

    @Override
    public boolean equals(Object obj) {
        return Objects.equals(coords, diameter);
    }

    @Override
    public String toString() {
        // this is for debugging only
        return Base58.encode(hashCode());

        // return "City " + coords;
    }

    /**
     * @param lot the lot to add
     */
    public void add(Lot lot) {
        this.lots.add(lot);
    }
}
