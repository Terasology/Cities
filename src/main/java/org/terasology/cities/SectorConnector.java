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
import org.terasology.cities.model.Sector;
import org.terasology.cities.model.Site;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

/**
 * Defines connections for a sector
 * @author Martin Steiger
 */
public class SectorConnector implements Function<Sector, Set<UnorderedPair<Site>>> {

    private final Function<Sector, Set<Site>> siteMap;
    private final Function<Site, Set<Site>> connectedCities;

    /**
     * @param siteMap defines sites in a sector
     * @param connectedCities defines pair-wise connections between sites
     */
    public SectorConnector(Function<Sector, Set<Site>> siteMap, Function<Site, Set<Site>> connectedCities) {
        this.siteMap = siteMap;
        this.connectedCities = connectedCities;
    }

    @Override
    public Set<UnorderedPair<Site>> apply(Sector sector) {
        Set<Site> sites = Sets.newHashSet(siteMap.apply(sector));

        Set<UnorderedPair<Site>> connections = Sets.newHashSet();
        
        for (Site site : sites) {
            Set<Site> conn = connectedCities.apply(site);
            
            for (Site other : conn) {
                connections.add(new UnorderedPair<Site>(site, other));
            }
        }
        
        return connections;
    }
}
