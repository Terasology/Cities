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

package org.terasology.cities.generator;

import java.util.Set;

import javax.vecmath.Point2i;

import org.terasology.cities.common.Orientation;
import org.terasology.cities.common.Point2iUtils;
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
        Point2i pos = city.getPos();

        for (City other : cities) {
            double distSq = Point2iUtils.distanceSquared(pos, other.getPos());
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

        Point2i pos1 = city.getPos();

        for (City other : neighbors) {
            Point2i pos2 = other.getPos();
            double distSq = Point2iUtils.distanceSquared(pos1, pos2);

            if (distSq < maxDist * maxDist) {
                closeCities.add(other);
            }
        }
        
        return closeCities;
    }


}
