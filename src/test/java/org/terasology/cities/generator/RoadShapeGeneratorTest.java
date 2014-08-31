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

import java.awt.Shape;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.vecmath.Point2i;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cities.AreaInfo;
import org.terasology.cities.CityTerrainComponent;
import org.terasology.cities.SectorConnector;
import org.terasology.cities.common.CachingFunction;
import org.terasology.cities.model.Junction;
import org.terasology.cities.model.Road;
import org.terasology.cities.model.Site;
import org.terasology.commonworld.Orientation;
import org.terasology.commonworld.Sector;
import org.terasology.commonworld.Sectors;
import org.terasology.commonworld.UnorderedPair;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.commonworld.heightmap.HeightMaps;

import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;

/**
 * Tests {@link SectorConnector}
 * @author Martin Steiger
 */
public class RoadShapeGeneratorTest  {

    private static final Logger logger = LoggerFactory.getLogger(RoadShapeGeneratorTest.class);
    
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
        
        final CityTerrainComponent config = new CityTerrainComponent();
        final HeightMap heightMap = HeightMaps.constant(10);
        final Function<Sector, AreaInfo> sectorInfos = CachingFunction.wrap(new Function<Sector, AreaInfo>() {

            @Override
            public AreaInfo apply(Sector input) {
                return new AreaInfo(config, heightMap);
            }
        }); 

        SiteFinderRandom cpr = new SiteFinderRandom(seed, sectorInfos, minPerSector, maxPerSector, minSize, maxSize);

        double maxDist = 800;
        Function<Site, Set<Site>> cc = new SiteConnector(cpr, maxDist);
        final Function<Sector, Set<UnorderedPair<Site>>> sc = CachingFunction.wrap(new SectorConnector(cpr, cc));

        final Function<UnorderedPair<Site>, Road> rg = CachingFunction.wrap(new Function<UnorderedPair<Site>, Road>() {
            private RoadGeneratorSimple rgs = new RoadGeneratorSimple(junctions);
            private RoadModifierRandom rmr = new RoadModifierRandom(0.01);

            @Override
            public Road apply(UnorderedPair<Site> input) {
                Road road = rgs.apply(input);
                rmr.apply(road);
                return road;
            }
            
        });
        
        Function<Sector, Set<Road>> roadMap = new Function<Sector, Set<Road>>() {

            @Override
            public Set<Road> apply(Sector sector) {
                Set<Road> allRoads = Sets.newHashSet();
                
                Set<UnorderedPair<Site>> localConns = sc.apply(sector);
                Set<UnorderedPair<Site>> allConns = Sets.newHashSet(localConns);
                
                // add all neighbors, because their roads might be passing through
                for (Orientation dir : Orientation.values()) {
                    Sector neighbor = sector.getNeighbor(dir);

                    allConns.addAll(sc.apply(neighbor));
                }

                for (UnorderedPair<Site> conn : allConns) {
                    Road road = rg.apply(conn);
                    allRoads.add(road);
                }

                return allRoads;
            }
        };
        
        roadMap = CachingFunction.wrap(roadMap);

        for (int x = 0; x < sectors; x++) {
            for (int z = 0; z < sectors; z++) {
                Sector sector = Sectors.getSector(x, z);
                roadMap.apply(sector);   // fill the cache
            }
        }
        
        logger.info("Generating road shapes for {} sectors", sectors * sectors);

        Function<Sector, Shape> roadShapeFunc = new RoadShapeGenerator(roadMap);
  
        Stopwatch pRoadShapes = Stopwatch.createStarted();

        for (int x = 0; x < sectors; x++) {
            for (int z = 0; z < sectors; z++) {
                Sector sector = Sectors.getSector(x, z);
                
                roadShapeFunc.apply(sector);
            }
        }
        
        logger.info("Created road shapes in {}ms.", pRoadShapes.elapsed(TimeUnit.MILLISECONDS));
    }
}
