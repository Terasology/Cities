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

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cities.SectorConnector;
import org.terasology.cities.common.Point2iUtils;
import org.terasology.cities.common.Profiler;
import org.terasology.cities.common.UnorderedPair;
import org.terasology.cities.model.City;
import org.terasology.cities.model.Sector;
import org.terasology.cities.model.Sectors;

import com.google.common.base.Function;

/**
 * Tests {@link SectorConnector}
 * @author Martin Steiger
 */
public class SectorConnectorTest  {

    private static final Logger logger = LoggerFactory.getLogger(SectorConnectorTest.class);
    
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

        double maxDist = 800;
        Function<City, Set<City>> cc = new CityConnector(cpr, maxDist);
        
        for (int x = 0; x < sectors; x++) {
            for (int z = 0; z < sectors; z++) {
                Sector sector = Sectors.getSector(x, z);
                cpr.apply(sector); // fill the cache
            }
        }
        
        SectorConnector sc = new SectorConnector(cpr, cc);

        Profiler.start("city-connector");

        int hits = 0;
        for (int x = 0; x < sectors; x++) {
            for (int z = 0; z < sectors; z++) {
                Sector sector = Sectors.getSector(x, z);
                
                Set<UnorderedPair<City>> conns = sc.apply(sector);
                hits += conns.size();
                
                // if only one connection exists, it may be longer -> keep cities connected
                if (conns.size() > 1) {
                    for (UnorderedPair<City> conn : conns) {
                        assertTrue("distance > maxDist", Point2iUtils.distance(conn.getA().getPos(), conn.getB().getPos()) <= maxDist);
                    }
                }
            }
        }
        
        logger.info("Created {} connections in {}", hits, Profiler.getAsString("city-connector"));
    }
}
