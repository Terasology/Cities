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

import java.awt.Shape;
import java.util.Set;

import javax.vecmath.Point2d;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cities.SectorConnector;
import org.terasology.cities.common.CachingFunction;
import org.terasology.cities.common.Orientation;
import org.terasology.cities.common.Profiler;
import org.terasology.cities.common.UnorderedPair;
import org.terasology.cities.generator.CityConnector;
import org.terasology.cities.generator.CityPlacerRandom;
import org.terasology.cities.generator.RoadGeneratorSimple;
import org.terasology.cities.generator.RoadModifierRandom;
import org.terasology.cities.generator.RoadShapeGenerator;
import org.terasology.cities.model.City;
import org.terasology.cities.model.Junction;
import org.terasology.cities.model.Road;
import org.terasology.cities.model.Sector;
import org.terasology.cities.model.Sectors;
import org.terasology.math.Vector2i;

import com.google.common.base.Function;
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

        final Function<Point2d, Junction> junctions = CachingFunction.wrap(new Function<Point2d, Junction>() {

            @Override
            public Junction apply(Point2d input) {
                return new Junction(input);
            }
            
        });
        
        CityPlacerRandom cpr = new CityPlacerRandom(seed, minPerSector, maxPerSector, minSize, maxSize);

        double maxDist = 0.8;
        Function<City, Set<City>> cc = new CityConnector(cpr, maxDist);
        final Function<Sector, Set<UnorderedPair<City>>> sc = CachingFunction.wrap(new SectorConnector(cpr, cc));

        final Function<UnorderedPair<City>, Road> rg = CachingFunction.wrap(new Function<UnorderedPair<City>, Road>() {
            private RoadGeneratorSimple rgs = new RoadGeneratorSimple(junctions);
            private RoadModifierRandom rmr = new RoadModifierRandom(0.01);

            @Override
            public Road apply(UnorderedPair<City> input) {
                Road road = rgs.apply(input);
                rmr.apply(road);
                return road;
            }
            
        });
        
        Function<Sector, Set<Road>> roadMap = new Function<Sector, Set<Road>>() {

            @Override
            public Set<Road> apply(Sector sector) {
                Set<Road> allRoads = Sets.newHashSet();
                
                Set<UnorderedPair<City>> localConns = sc.apply(sector);
                Set<UnorderedPair<City>> allConns = Sets.newHashSet(localConns);
                
                // add all neighbors, because their roads might be passing through
                for (Orientation dir : Orientation.values()) {
                    Sector neighbor = sector.getNeighbor(dir);

                    allConns.addAll(sc.apply(neighbor));
                }

                for (UnorderedPair<City> conn : allConns) {
                    Road road = rg.apply(conn);
                    allRoads.add(road);
                }

                return allRoads;
            }
        };
        
        roadMap = CachingFunction.wrap(roadMap);

        for (int x = 0; x < sectors; x++) {
            for (int z = 0; z < sectors; z++) {
                Vector2i coord = new Vector2i(x, z);
                Sector sector = Sectors.getSector(coord);
                roadMap.apply(sector);   // fill the cache
            }
        }
        
        logger.info("Generating road shapes for {} sectors", sectors * sectors);

        Function<Sector, Shape> roadShapeFunc = new RoadShapeGenerator(roadMap);
  
        Profiler.start("road-shapes");

        for (int x = 0; x < sectors; x++) {
            for (int z = 0; z < sectors; z++) {
                Vector2i coord = new Vector2i(x, z);
                Sector sector = Sectors.getSector(coord);
                
                roadShapeFunc.apply(sector);
            }
        }
        
        logger.info("Created road shapes in {}", Profiler.getAsString("road-shapes"));
    }
}
