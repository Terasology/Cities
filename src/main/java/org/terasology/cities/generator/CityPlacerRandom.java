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

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import javax.vecmath.Point2i;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cities.SectorInfo;
import org.terasology.cities.common.Point2iUtils;
import org.terasology.cities.model.City;
import org.terasology.cities.model.MedievalTown;
import org.terasology.cities.model.Sector;
import org.terasology.cities.testing.NameList;
import org.terasology.math.TeraMath;
import org.terasology.namegenerator.logic.generators.Markov2NameGenerator;
import org.terasology.namegenerator.logic.generators.NameGenerator;
import org.terasology.utilities.random.FastRandom;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

/**
 * Creates a set of cities for a given sector.
 * Cities are places randomly.
 * It checks for minimum distance to other cities in the SAME sector, but not others.
 * @author Martin Steiger
 */
public class CityPlacerRandom implements Function<Sector, Set<City>> {

    private static final Logger logger = LoggerFactory.getLogger(CityPlacerRandom.class);
    
    private final String seed;

    private final int maxSize;
    private final int minSize;
    
    private final int minPerSector;
    private final int maxPerSector;

    private Function<Sector, SectorInfo> sectorInfos;

    /**
     * @param seed the seed
     * @param sectorInfos a function for sector infos
     * @param minPerSector minimum settlements per sector
     * @param maxPerSector maximum settlements per sector
     * @param minSize minimum settlement size
     * @param maxSize maximum settlement size 
     */
    public CityPlacerRandom(String seed, Function<Sector, SectorInfo> sectorInfos, int minPerSector, int maxPerSector, int minSize, int maxSize) {
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
    public Set<City> apply(Sector sector) {
        final int maxTries = 3;
        
        // create deterministic random
        Point2i sc = sector.getCoords();
        int hash = Objects.hash(seed, sector);
        FastRandom fr = new FastRandom(hash);

        Set<City> result = Sets.newHashSet();
        
        int count = fr.nextInt(minPerSector, maxPerSector);

        logger.debug("Creating {} cities in {}", count, sector);
        
        NameGenerator nameGen = new Markov2NameGenerator(hash, Arrays.asList(NameList.NAMES));
        SectorInfo si = sectorInfos.apply(sector);
        
        for (int i = 0; i < count; i++) {

            // try n times to randomly place a new city and check the distance to existing ones
            City ci;
            int tries = maxTries;
            do {
                double nx = sc.x + fr.nextDouble();
                double nz = sc.y + fr.nextDouble();
                
                // make smaller cities more probable than larger cities
                double size = fr.nextDouble(Math.sqrt(minSize), Math.sqrt(maxSize));
                size = size * size;
    
                int cx = TeraMath.floorToInt(nx * Sector.SIZE + 0.5);
                int cz = TeraMath.floorToInt(nz * Sector.SIZE + 0.5);
                
                String name = nameGen.nextName(5, 10);
                ci = new MedievalTown(name, size, cx, cz);
                tries--;
                
            } while (!placementOk(ci, si, result) && tries >= 0);            

            if (tries < 0) {
                logger.debug("Could not place a new city at {}", sector);
            } else {
                logger.debug("{} - Creating city {} at {}", sector.toString(), ci.toString(), ci.getPos());
                result.add(ci);
            }
        }

        return result;
    }

    private boolean placementOk(City city, SectorInfo si, Set<City> others) {
        final double minDistToOthers = 200;
        
        if (!distanceToOthersOk(city, others, minDistToOthers)) {
            return false;
        }
        
        if (si.isBlocked(city.getPos())) {
            return false;
        }
        
        return true;
    }
    
    private boolean distanceToOthersOk(City city, Set<City> cities, double minDist) {
            
        Point2i pos = city.getPos();

        for (City other : cities) {
            double distSq = Point2iUtils.distanceSquared(pos, other.getPos());
            if (distSq < minDist * minDist) {
                return false;
            }
        }
        
        return true;
    }

}
