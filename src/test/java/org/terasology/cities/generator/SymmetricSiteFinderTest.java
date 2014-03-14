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

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Set;

import javax.vecmath.Point2i;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cities.AreaInfo;
import org.terasology.cities.CityTerrainComponent;
import org.terasology.cities.common.CachingFunction;
import org.terasology.cities.heightmap.HeightMaps;
import org.terasology.cities.heightmap.NoiseHeightMap;
import org.terasology.cities.heightmap.SymmetricHeightMap;
import org.terasology.cities.model.Sector;
import org.terasology.cities.model.Sectors;
import org.terasology.cities.model.Site;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

/**
 * Tests {@link SiteFinderRandom}
 * @author Martin Steiger
 */
public class SymmetricSiteFinderTest  {

    private static final Logger logger = LoggerFactory.getLogger(SymmetricSiteFinderTest.class);
    
    /**
     * Performs through tests and logs computation time and results
     */
    @Test
    public void test() {
        String seed = "asd";
        
        int minPerSector = 1;
        int maxPerSector = 3;
        int minSize = 10;
        int maxSize = 100;
        
        final CityTerrainComponent config = new CityTerrainComponent();
        final SymmetricHeightMap heightMap = HeightMaps.symmetricAlongDiagonal(new NoiseHeightMap(seed));
        final Function<Sector, AreaInfo> sectorInfos = CachingFunction.wrap(new Function<Sector, AreaInfo>() {

            @Override
            public AreaInfo apply(Sector input) {
                return new AreaInfo(config, heightMap);
            }
        }); 
        
        SiteFinderRandom cpr = new SiteFinderRandom(seed, sectorInfos, minPerSector, maxPerSector, minSize, maxSize);

        SymmetricSiteFinder symFinder = new SymmetricSiteFinder(cpr, heightMap);
        
        Sector sectorA = Sectors.getSector(4, 8);
        Sector sectorA2 = Sectors.getSector(-8, -4);

        Set<Site> sitesA = symFinder.apply(sectorA);
        Set<Site> sitesA2 = symFinder.apply(sectorA2);
        
        Function<Site, Point2i> site2pos = new Function<Site, Point2i>() {

            @Override
            public Point2i apply(Site input) {
                return input.getPos();
            }
            
        };
        
        Collection<Point2i> posA = Collections2.transform(sitesA, site2pos);
        Collection<Point2i> posA2 = Collections2.transform(sitesA2, site2pos);
        
        for (Point2i pos : posA) {
            Point2i mirrored = heightMap.getMirrored(pos);
            assertTrue(posA2.contains(mirrored));
        }
    }
}
