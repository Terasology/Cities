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

import java.util.HashSet;
import java.util.Set;

import javax.vecmath.Point2i;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cities.common.Point2iUtils;
import org.terasology.cities.model.Sector;
import org.terasology.cities.model.Sectors;
import org.terasology.cities.model.Site;
import org.terasology.cities.symmetry.Symmetry;

import com.google.common.base.Function;

/**
 * Creates a symmetric set of settlements based on a symmetric height map
 * @author Martin Steiger
 */
public class SymmetricSiteFinder implements Function<Sector, Set<Site>> {

    private static final Logger logger = LoggerFactory.getLogger(SymmetricSiteFinder.class);

    private Function<Sector, Set<Site>> baseFinder;

    private Symmetry symmetry;

    /**
     * @param baseFinder the original site finder
     * @param symmetry the symmetric height map
     */
    public SymmetricSiteFinder(Function<Sector, Set<Site>> baseFinder, Symmetry symmetry) {
        this.baseFinder = baseFinder;
        this.symmetry = symmetry;

    }

    /**
     * @param sector the sector
     * @return a set of cities
     */
    @Override
    public Set<Site> apply(Sector sector) {
        int minDist = 200;

        // create deterministic random
        Point2i secPos = sector.getCoords();

        Point2i mirrSecPos = symmetry.getMirrored(secPos);
        Set<Site> result = new HashSet<>();

        if (secPos.equals(mirrSecPos)) {
            Set<Site> base = baseFinder.apply(sector);

            for (Site site : base) {
                Point2i pos = site.getPos();

                Point2i newPos = symmetry.getMirrored(pos);
                Site mirrorSite = new Site(newPos.getX(), newPos.getY(), site.getRadius());

                if (distanceToOthersOk(site, result, minDist) && distanceToOthersOk(mirrorSite, result, minDist)) {

                    // check if distance to its own mirror site is ok
                    double distSq = Point2iUtils.distanceSquared(pos, newPos);
                    if (distSq > minDist * minDist) {
                        result.add(mirrorSite);
                        result.add(site);
                    }
                }

                if (result.size() >= base.size()) {
                    break;
                }
            }

            return result;
        }

        if (!symmetry.isMirrored(secPos)) {
            Set<Site> base = baseFinder.apply(sector);

            for (Site site : base) {
                Point2i pos = site.getPos();
                Point2i newPos = symmetry.getMirrored(pos);

                // check if distance to its own mirror site is ok
                double distSq = Point2iUtils.distanceSquared(pos, newPos);
                if (distSq > minDist * minDist) {
                    result.add(site);
                }
            }
            return result;

        } else {
            Sector mirrorSector = Sectors.getSector(mirrSecPos);
            Set<Site> base = baseFinder.apply(mirrorSector);

            for (Site site : base) {
                Point2i pos = site.getPos();
                Point2i newPos = symmetry.getMirrored(pos);
                Site mirrorSite = new Site(newPos.getX(), newPos.getY(), site.getRadius());

                // check if distance to its own mirror site is ok
                double distSq = Point2iUtils.distanceSquared(pos, newPos);
                if (distSq > minDist * minDist) {
                    result.add(mirrorSite);
                }
            }

            logger.debug("Creating {} mirrored sites in {}", result.size(), sector);
            return result;
        }
    }

    private boolean distanceToOthersOk(Site city, Set<Site> others, double minDist) {

        Point2i pos = city.getPos();

        for (Site other : others) {
            double distSq = Point2iUtils.distanceSquared(pos, other.getPos());
            if (distSq < minDist * minDist) {
                return false;
            }
        }

        return true;
    }
}
