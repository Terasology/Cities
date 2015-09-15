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

package org.terasology.cities.generator;

import java.util.Set;

import org.terasology.cities.model.Site;
import org.terasology.commonworld.Orientation;
import org.terasology.commonworld.Sector;
import org.terasology.math.geom.BaseVector2i;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;

/**
 * Defines connections of a site to other sites in the same and neighboring sectors.
 * All sites within a given radius are connected. If none are found the closest site
 * is connected.
 */
public class SiteConnector implements Function<Site, Set<Site>> {

    private final Function<Sector, Set<Site>> siteMap;
    private final double maxDist;

    /**
     * @param siteMap a function that defines sites
     * @param maxDist the maximum distance between two connected sites
     */
    public SiteConnector(Function<Sector, Set<Site>> siteMap, double maxDist) {
        this.siteMap = siteMap;
        this.maxDist = maxDist;
    }

    /**
     * @param site the site of interest
     * @return a set of connected sites (including those in neighbor sectors)
     */
    @Override
    public Set<Site> apply(Site site) {

        Sector sector = site.getSector();

        Set<Site> directNeighs = siteMap.apply(sector);
        Set<Site> sites = Sets.newHashSet(directNeighs);

        for (Orientation dir : Orientation.values()) {
            Sector neighbor = sector.getNeighbor(dir);
            Set<Site> neighCities = siteMap.apply(neighbor);
            sites.addAll(neighCities);
        }

        sites.remove(site);

        Set<Site> result = sitesInRange(site, sites);

        if (result.isEmpty()) {
            Optional<Site> closest = getClosest(site, sites);
            if (closest.isPresent()) {
                result.add(closest.get());
            }
        }

        return result;
    }

    /**
     * @param site the site
     * @param sites a set of sites in the neighborhood
     * @return the closest site from the set
     */
    private Optional<Site> getClosest(Site site, Set<Site> sites) {

        double minDist = Double.MAX_VALUE;
        Optional<Site> best = Optional.absent();
        BaseVector2i pos = site.getPos();

        for (Site other : sites) {
            double distSq = pos.distanceSquared(other.getPos());
            if (distSq < minDist) {
                minDist = distSq;
                best = Optional.of(other);
            }
        }

        return best;
    }

    /**
     * @param site the site
     * @param neighbors a set of sites in the neighborhood
     * @return all sites within maxDist
     */
    private Set<Site> sitesInRange(Site site, Set<Site> neighbors) {
        Set<Site> closeCities = Sets.newHashSet();

        BaseVector2i pos1 = site.getPos();

        for (Site other : neighbors) {
            BaseVector2i pos2 = other.getPos();
            double distSq = pos1.distanceSquared(pos2);

            if (distSq < maxDist * maxDist) {
                closeCities.add(other);
            }
        }

        return closeCities;
    }


}
