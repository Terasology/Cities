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

package org.terasology.cities.generator;

import java.util.Set;

import javax.vecmath.Point2d;

import org.terasology.cities.common.Orientation;
import org.terasology.cities.model.City;
import org.terasology.cities.model.Sector;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;

/**
 * Defines connections of a city to other cities in the same and neighboring sectors.
 * All cities within a given radius are connected. If none are found the closest city
 * is connected.
 * @author Martin Steiger
 */
public class CityConnector implements Function<City, Set<City>> {
  
    private final Function<Sector, Set<City>> cityMap;
    private final double maxDist;

    /**
     * @param cityMap a function that defines cities
     * @param maxDist the maximum distance between two connected cities
     */
    public CityConnector(Function<Sector, Set<City>> cityMap, double maxDist) {
        this.cityMap = cityMap;
        this.maxDist = maxDist;
    }
    
    /**
     * @param city the city of interest
     * @return a set of connected cities (including those in neighbor sectors)
     */
    @Override
    public Set<City> apply(City city) {
        
        Sector sector = city.getSector();
        
        Set<City> directNeighs = cityMap.apply(sector);
        Set<City> cities = Sets.newHashSet(directNeighs); 

        for (Orientation dir : Orientation.values()) {
            Sector neighbor = sector.getNeighbor(dir);
            Set<City> neighCities = cityMap.apply(neighbor);
            cities.addAll(neighCities);
        }
        
        cities.remove(city);
        
        Set<City> result = citiesInRange(city, cities);
        
        if (result.isEmpty()) {
            Optional<City> closest = getClosest(city, cities);
            if (closest.isPresent()) {
                result.add(closest.get());
            }
        }
        
        return result;
    }

    /**
     * @param city the city
     * @param cities a set of cities in the neighborhood
     * @return the closest city from the set
     */
    private Optional<City> getClosest(City city, Set<City> cities) {
        
        double minDist = Double.MAX_VALUE;
        Optional<City> best = Optional.absent();
        Point2d pos = city.getPos();

        for (City other : cities) {
            double distSq = pos.distanceSquared(other.getPos());
            if (distSq < minDist) {
                minDist = distSq;
                best = Optional.of(other);
            }
        }

        return best;
    }

    /**
     * @param city the city
     * @param neighbors a set of cities in the neighborhood
     * @return all cities within maxDist
     */
    private Set<City> citiesInRange(City city, Set<City> neighbors) {
        Set<City> closeCities = Sets.newHashSet(); 

        Point2d pos1 = city.getPos();

        for (City other : neighbors) {
            Point2d pos2 = other.getPos();
            double distSq = pos1.distanceSquared(pos2);

            if (distSq < maxDist * maxDist) {
                closeCities.add(other);
            }
        }
        
        return closeCities;
    }


}
