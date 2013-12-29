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

import java.util.Objects;
import java.util.Set;

import javax.vecmath.Point2i;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cities.common.Point2iUtils;
import org.terasology.cities.model.City;
import org.terasology.cities.model.MedievalTown;
import org.terasology.cities.model.Sector;
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
public class CityPlacerRandom implements Function<Sector, Set<City>> {

    private static final Logger logger = LoggerFactory.getLogger(CityPlacerRandom.class);
    
    private final String seed;

    private final int maxSize;
    private final int minSize;
    
    private final int minPerSector;
    private final int maxPerSector;

    /**
     * @param seed the seed
     * @param minPerSector minimum settlements per sector
     * @param maxPerSector maximum settlements per sector
     * @param minSize minimum settlement size
     * @param maxSize maximum settlement size 
     */
    public CityPlacerRandom(String seed, int minPerSector, int maxPerSector, int minSize, int maxSize) {
        this.seed = seed;
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
                
                ci = new MedievalTown(size, cx, cz);
                tries--;
                
            } while (!placementOk(ci, result) && tries >= 0);            

            if (tries < 0) {
                logger.debug("Could not place a new city at {}", sector);
            } else {
                logger.debug("{} - Creating city {} at {}", sector.toString(), ci.toString(), ci.getPos());
                result.add(ci);
            }
        }

        return result;
    }

    private boolean placementOk(City city, Set<City> others) {
        final double minDistToOthers = 200;
        final double minDistToWater = 100;
        
        if (!distanceToOthersOk(city, others, minDistToOthers)) {
            return false;
        }
        
        if (!distanceToWaterOk(city, minDistToWater)) {
            return false;
        }
        
        return true;
    }
    
    @SuppressWarnings("unused")
    private boolean distanceToWaterOk(City city, double minDistToWater) {
        // TODO: we need terrain info here to check this
        
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
