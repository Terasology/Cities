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

import javax.vecmath.Point2d;

import org.terasology.cities.common.Base58;
import org.terasology.math.Vector2i;

/**
 * Provides information on a city
 * @author Martin Steiger
 */
public class City {

    private double diameter;
    private Point2d coords;

    /**
     * @param size the city cize (number of habitants)
     * @param x the x coord (in sectors)
     * @param z the z coord (in sectors)
     */
    public City(double size, double x, double z) {
        this.diameter = size;
        this.coords = new Point2d(x, z);
    }

    /**
     * @return the city center in sectors
     */
    public Point2d getPos() {
        return coords;
    }
    /**
     * @return the city center in sectors
     */
    public Sector getSector() {
        int sx = (int) Math.floor(coords.x);
        int sy = (int) Math.floor(coords.y);
        return Sectors.getSector(new Vector2i(sx, sy));
    }

    /**
     * @return the diameter of the city (in blocks)
     */
    public double getDiameter() {
        return this.diameter;
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
}
