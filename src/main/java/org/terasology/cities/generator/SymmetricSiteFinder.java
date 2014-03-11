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
import java.util.Objects;
import java.util.Set;

import javax.vecmath.Point2i;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cities.AreaInfo;
import org.terasology.cities.common.Point2iUtils;
import org.terasology.cities.heightmap.SymmetricHeightMap;
import org.terasology.cities.model.Sector;
import org.terasology.cities.model.Sectors;
import org.terasology.cities.model.Site;
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
public class SymmetricSiteFinder implements Function<Sector, Set<Site>> {

    private static final Logger logger = LoggerFactory.getLogger(SymmetricSiteFinder.class);
    
    private Function<? super Sector, AreaInfo> sectorInfos;

	private Function<Sector, Set<Site>> baseFinder;

	private SymmetricHeightMap heightMap;
	
    /**
     * @param maxSize maximum settlement size 
     */
    public SymmetricSiteFinder(Function<Sector, Set<Site>> baseFinder, SymmetricHeightMap heightMap) {
        this.baseFinder = baseFinder;
        this.heightMap = heightMap;
        
    }

    /**
     * @param sector the sector
     * @return a set of cities
     */
    @Override
    public Set<Site> apply(Sector sector) {
        int minDist = 200;
    	
        // create deterministic random
        Point2i sc = sector.getCoords();

    	Point2i mirrPos = heightMap.getMirrored(sc);
        Set<Site> base = baseFinder.apply(sector);
    	Set<Site> result = new HashSet<>(base.size());


    	if (sc.equals(mirrPos)) {
    		
	        for (Site site : base) {
	        	Point2i pos = site.getPos();

                Point2i newPos = heightMap.getMirrored(pos);
	        	Site mirrorSite = new Site(newPos.getX(), newPos.getY(), site.getRadius());
	        		
	        	if (distanceToOthersOk(site, result, minDist) && distanceToOthersOk(mirrorSite, result, minDist)) {
	        		
					// check if distance to its own mirror site is ok
	        		double distSq = Point2iUtils.distanceSquared(pos, newPos);
	                if (distSq < minDist * minDist) {
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
    	
    	if (!heightMap.isMirrored(sc)) {
	        for (Site site : base) {
	        	Point2i pos = site.getPos();
	        	Point2i newPos = heightMap.getMirrored(pos);
	        	Site mirrorSite = new Site(newPos.getX(), newPos.getY(), site.getRadius());
	        		
				// check if distance to its own mirror site is ok
        		double distSq = Point2iUtils.distanceSquared(pos, newPos);
                if (distSq < minDist * minDist) {
                	result.add(site);
                }
	        }

    	} else {
		
	        for (Site site : base) {
	        	Point2i pos = site.getPos();
	        	Point2i newPos = heightMap.getMirrored(pos);
	        	Site mirrorSite = new Site(newPos.getX(), newPos.getY(), site.getRadius());
	        		
				// check if distance to its own mirror site is ok
        		double distSq = Point2iUtils.distanceSquared(pos, newPos);
                if (distSq < minDist * minDist) {
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
