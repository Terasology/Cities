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

import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cities.CityWorldConfig;
import org.terasology.cities.AreaInfo;
import org.terasology.cities.common.CachingFunction;
import org.terasology.cities.common.Profiler;
import org.terasology.cities.model.City;
import org.terasology.cities.model.Sector;
import org.terasology.cities.model.Sectors;
import org.terasology.cities.terrain.HeightMap;
import org.terasology.cities.terrain.HeightMaps;

import com.google.common.base.Function;

/**
 * Tests {@link CityPlacerRandom}
 * @author Martin Steiger
 */
public class CityPlacerRandomTest  {

    private static final Logger logger = LoggerFactory.getLogger(CityPlacerRandomTest.class);
    
    /**
     * Performs through tests and logs computation time and results
     */
    @Test
    public void test() {
        String seed = "asd";
        int sectors = 10;       // sectors * sectors will be created
        
        int minPerSector = 1;
        int maxPerSector = 3;
        int minSize = 10;
        int maxSize = 100;
        
        final CityWorldConfig config = new CityWorldConfig();
        final HeightMap heightMap = HeightMaps.constant(10);
        final Function<Sector, AreaInfo> sectorInfos = CachingFunction.wrap(new Function<Sector, AreaInfo>() {

            @Override
            public AreaInfo apply(Sector input) {
                return new AreaInfo(config, heightMap);
            }
        }); 
        
        CityPlacerRandom cpr = new CityPlacerRandom(seed, sectorInfos, minPerSector, maxPerSector, minSize, maxSize);
        
        Profiler.start("city-placement");
        
        int bins = maxPerSector - minPerSector + 1;
        
        int[] hits = new int[bins];
        for (int x = 0; x < sectors; x++) {
            for (int z = 0; z < sectors; z++) {
                Set<City> cities = cpr.apply(Sectors.getSector(x, z));
                
                assertTrue(cities.size() >= minPerSector);
                assertTrue(cities.size() <= maxPerSector);

                int bin = cities.size() - minPerSector;
                hits[bin]++;

                for (City city : cities) {
                    assertTrue(city.getDiameter() >= minSize);
                    assertTrue(city.getDiameter() <= maxSize);
                }
            }
        }

        int sum = 0;
        for (int bin : hits) {
            sum += bin;
        }
        
        assertTrue(sum <= sectors * sectors);
        
        logger.info("Created {} sectors in {}", sum, Profiler.getAsString("city-placement"));
        for (int i = 0; i < bins; i++) {
            logger.info("Created {} sectors with {} cities", hits[i], i + minPerSector);
        }
    }
}
