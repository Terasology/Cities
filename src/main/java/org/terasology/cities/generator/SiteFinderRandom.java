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

import java.util.Objects;
import java.util.Set;

import javax.vecmath.Point2i;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cities.AreaInfo;
import org.terasology.cities.model.Site;
import org.terasology.commonworld.Sector;
import org.terasology.commonworld.geom.Point2iUtils;
import org.terasology.math.TeraMath;
import org.terasology.utilities.random.FastRandom;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

/**
 * Creates a set of cities for a given sector.
 * Cities are places randomly.
 * It checks for minimum distance to other cities in the SAME sector, but not others.
 * @author Martin Steiger
 */
public class SiteFinderRandom implements Function<Sector, Set<Site>> {

    private static final Logger logger = LoggerFactory.getLogger(SiteFinderRandom.class);
    
    private final String seed;

    private final int maxSize;
    private final int minSize;
    
    private final int minPerSector;
    private final int maxPerSector;

    private Function<? super Sector, AreaInfo> sectorInfos;

    /**
     * @param seed the seed
     * @param sectorInfos a function for sector infos
     * @param minPerSector minimum settlements per sector
     * @param maxPerSector maximum settlements per sector
     * @param minSize minimum settlement size
     * @param maxSize maximum settlement size 
     */
    public SiteFinderRandom(String seed, Function<? super Sector, AreaInfo> sectorInfos, int minPerSector, int maxPerSector, int minSize, int maxSize) {
        this.seed = seed;
        this.sectorInfos = sectorInfos;
        this.minPerSector = minPerSector;
        this.maxPerSector = maxPerSector;
        this.minSize = minSize;
        this.maxSize = maxSize;
    }

    /**
     * @param sector the sector
     * @return a set of cities
     */
    @Override
    public Set<Site> apply(Sector sector) {
        final int maxTries = 3;
        
        // create deterministic random
        Point2i sc = sector.getCoords();
        int hash = Objects.hash(seed, sector);
        FastRandom fr = new FastRandom(hash);

        Set<Site> result = Sets.newHashSet();
        
        int count = fr.nextInt(minPerSector, maxPerSector);

        logger.debug("Creating {} sites in {}", count, sector);
        
        AreaInfo si = sectorInfos.apply(sector);
        
        for (int i = 0; i < count; i++) {

            // try n times to randomly place a new city and check the distance to existing ones
            Site site;
            int tries = maxTries;
            do {
                double nx = sc.x + fr.nextDouble();
                double nz = sc.y + fr.nextDouble();
                
                // make smaller cities more probable than larger cities
                double size = fr.nextDouble(Math.sqrt(minSize), Math.sqrt(maxSize));
                size = size * size;
    
                int cx = TeraMath.floorToInt(nx * Sector.SIZE + 0.5);
                int cz = TeraMath.floorToInt(nz * Sector.SIZE + 0.5);
                
                site = new Site(cx, cz, (int) size / 2);
                tries--;
                
            } while (!placementOk(site, si, result) && tries >= 0);            

            if (tries < 0) {
                logger.debug("Could not place a new site at {}", sector);
            } else {
                logger.debug("{} - Creating site {} at {}", sector.toString(), site.toString(), site.getPos());
                result.add(site);
            }
        }

        return result;
    }

    private boolean placementOk(Site site, AreaInfo si, Set<Site> others) {
        final double minDistToOthers = 200;
        
        if (!distanceToOthersOk(site, others, minDistToOthers)) {
            return false;
        }
        
        if (si.isBlocked(site.getPos())) {
            return false;
        }
        
        return true;
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
