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
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cities.AreaInfo;
import org.terasology.cities.CityTerrainComponent;
import org.terasology.cities.model.Site;
import org.terasology.cities.common.CachingFunction;
import org.terasology.commonworld.Sector;
import org.terasology.commonworld.Sectors;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.commonworld.heightmap.HeightMaps;

import com.google.common.base.Function;
import com.google.common.base.Stopwatch;

/**
 * Tests {@link SiteFinderRandom}
 * @author Martin Steiger
 */
public class SiteFinderRandomTest  {

    private static final Logger logger = LoggerFactory.getLogger(SiteFinderRandomTest.class);

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

        final CityTerrainComponent config = new CityTerrainComponent();
        final HeightMap heightMap = HeightMaps.constant(10);
        final Function<Sector, AreaInfo> sectorInfos = CachingFunction.wrap(new Function<Sector, AreaInfo>() {

            @Override
            public AreaInfo apply(Sector input) {
                return new AreaInfo(config, heightMap);
            }
        });

        SiteFinderRandom cpr = new SiteFinderRandom(seed, sectorInfos, minPerSector, maxPerSector, minSize, maxSize);

        Stopwatch pPlacement = Stopwatch.createStarted();

        int bins = maxPerSector - minPerSector + 1;

        int[] hits = new int[bins];
        for (int x = 0; x < sectors; x++) {
            for (int z = 0; z < sectors; z++) {
                Set<Site> sites = cpr.apply(Sectors.getSector(x, z));

                assertTrue(sites.size() >= minPerSector);
                assertTrue(sites.size() <= maxPerSector);

                int bin = sites.size() - minPerSector;
                hits[bin]++;

                for (Site site : sites) {
                    assertTrue(site.getRadius() * 2 >= minSize);
                    assertTrue(site.getRadius() * 2 <= maxSize);
                }
            }
        }

        int sum = 0;
        for (int bin : hits) {
            sum += bin;
        }

        assertTrue(sum <= sectors * sectors);

        logger.info("Created {} sectors in {}ms.", sum, pPlacement.elapsed(TimeUnit.MILLISECONDS));
        for (int i = 0; i < bins; i++) {
            logger.info("Created {} sectors with {} cities", hits[i], i + minPerSector);
        }
    }
}
