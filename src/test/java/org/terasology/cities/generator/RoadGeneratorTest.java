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

import java.util.Set;

import javax.vecmath.Point2i;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cities.CityWorldConfig;
import org.terasology.cities.SectorConnector;
import org.terasology.cities.SectorInfo;
import org.terasology.cities.common.CachingFunction;
import org.terasology.cities.common.Profiler;
import org.terasology.cities.common.UnorderedPair;
import org.terasology.cities.model.City;
import org.terasology.cities.model.Junction;
import org.terasology.cities.model.Road;
import org.terasology.cities.model.Sector;
import org.terasology.cities.model.Sectors;
import org.terasology.cities.terrain.ConstantHeightMap;
import org.terasology.cities.terrain.HeightMap;

import com.google.common.base.Function;

/**
 * Tests {@link SectorConnector}
 * @author Martin Steiger
 */
public class RoadGeneratorTest  {

    private static final Logger logger = LoggerFactory.getLogger(RoadGeneratorTest.class);
    
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

        final Function<Point2i, Junction> junctions = CachingFunction.wrap(new Function<Point2i, Junction>() {

            @Override
            public Junction apply(Point2i input) {
                return new Junction(input);
            }
            
        });
        
        final CityWorldConfig config = new CityWorldConfig();
        final HeightMap heightMap = new ConstantHeightMap(10);
        final Function<Sector, SectorInfo> sectorInfos = CachingFunction.wrap(new Function<Sector, SectorInfo>() {

            @Override
            public SectorInfo apply(Sector input) {
                return new SectorInfo(input, config, heightMap);
            }
        }); 

        CityPlacerRandom cpr = new CityPlacerRandom(seed, sectorInfos, minPerSector, maxPerSector, minSize, maxSize);

        double maxDist = 800;
        Function<City, Set<City>> cc = new CityConnector(cpr, maxDist);
        Function<Sector, Set<UnorderedPair<City>>> sc = new SectorConnector(cpr, cc);
        sc = CachingFunction.wrap(sc);

        Function<UnorderedPair<City>, Road> rg = new Function<UnorderedPair<City>, Road>() {
            private RoadGeneratorSimple rgs = new RoadGeneratorSimple(junctions);
            private RoadModifierRandom rmr = new RoadModifierRandom(0.01);

            @Override
            public Road apply(UnorderedPair<City> input) {
                Road road = rgs.apply(input);
                rmr.apply(road);
                return road;
            }
            
        };

        for (int x = 0; x < sectors; x++) {
            for (int z = 0; z < sectors; z++) {
                Sector sector = Sectors.getSector(x, z);
                sc.apply(sector);   // fill the cache
            }
        }
        
        logger.info("Generating roads for {} sectors", sectors * sectors);
        
        Profiler.start("road-gen");

        int hits = 0;
        for (int x = 0; x < sectors; x++) {
            for (int z = 0; z < sectors; z++) {
                Sector sector = Sectors.getSector(x, z);
                
                Set<UnorderedPair<City>> conns = sc.apply(sector);
                hits += conns.size();
                
                for (UnorderedPair<City> conn : conns) {
                    rg.apply(conn);
                }
            }
        }
        
        logger.info("Created {} roads in {}", hits, Profiler.getAsString("road-gen"));
    }
}
