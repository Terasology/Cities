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
