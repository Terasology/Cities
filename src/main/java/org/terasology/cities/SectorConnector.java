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

package org.terasology.cities;

import java.util.Set;

import org.terasology.cities.common.UnorderedPair;
import org.terasology.cities.model.City;
import org.terasology.cities.model.Sector;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

/**
 * Defines connections for a sector
 * @author Martin Steiger
 */
public class SectorConnector implements Function<Sector, Set<UnorderedPair<City>>> {

    private final Function<Sector, Set<City>> cityMap;
    private final Function<City, Set<City>> connectedCities;

    /**
     * @param cityMap defines cities in a sector
     * @param connectedCities defines pair-wise connections between cities
     */
    public SectorConnector(Function<Sector, Set<City>> cityMap, Function<City, Set<City>> connectedCities) {
        this.cityMap = cityMap;
        this.connectedCities = connectedCities;
    }

    @Override
    public Set<UnorderedPair<City>> apply(Sector sector) {
        Set<City> cities = Sets.newHashSet(cityMap.apply(sector));

        Set<UnorderedPair<City>> connections = Sets.newHashSet();
        
        for (City city : cities) {
            Set<City> conn = connectedCities.apply(city);
            
            for (City other : conn) {
                connections.add(new UnorderedPair<City>(city, other));
            }
        }
        
        return connections;
    }
}
