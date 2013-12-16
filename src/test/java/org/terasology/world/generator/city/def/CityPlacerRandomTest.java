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

package org.terasology.world.generator.city.def;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cities.common.Profiler;
import org.terasology.cities.generator.CityPlacerRandom;
import org.terasology.cities.model.City;
import org.terasology.cities.model.Sectors;
import org.terasology.math.Vector2i;

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
        CityPlacerRandom cpr = new CityPlacerRandom(seed, minPerSector, maxPerSector, minSize, maxSize);
        
        Profiler.start("city-placement");
        
        int bins = maxPerSector - minPerSector + 1;
        
        int[] hits = new int[bins];
        for (int x = 0; x < sectors; x++) {
            for (int z = 0; z < sectors; z++) {
                Vector2i coord = new Vector2i(x, z);
                Set<City> cities = cpr.apply(Sectors.getSector(coord));
                
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
