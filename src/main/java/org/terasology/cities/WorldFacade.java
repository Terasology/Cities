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

package org.terasology.cities;

import java.awt.Shape;
import java.util.Set;

import javax.vecmath.Point2d;

import org.terasology.cities.common.CachingFunction;
import org.terasology.cities.common.UnorderedPair;
import org.terasology.cities.generator.CityConnector;
import org.terasology.cities.generator.CityPlacerRandom;
import org.terasology.cities.generator.LotGeneratorRandom;
import org.terasology.cities.generator.RoadGeneratorSimple;
import org.terasology.cities.generator.RoadModifierRandom;
import org.terasology.cities.generator.RoadShapeGenerator;
import org.terasology.cities.model.City;
import org.terasology.cities.model.Junction;
import org.terasology.cities.model.Road;
import org.terasology.cities.model.Sector;
import org.terasology.cities.model.SimpleLot;
import org.terasology.cities.model.Sector.Orientation;
import org.terasology.math.Vector2i;
import org.terasology.utilities.procedural.Noise;
import org.terasology.utilities.procedural.PerlinNoise;
import org.terasology.utilities.procedural.SimplexNoise;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

/**
 * Provides many different getters to rasterize a world
 * @author Martin Steiger
 */
public class WorldFacade {

    Function<Sector, Set<City>> cityMap;

    Function<City, Set<City>> connectedCities;

    private Function<Sector, Set<UnorderedPair<City>>> sectorConnections;

    private Function<Point2d, Junction> junctions;

    private Function<Sector, Set<Road>> roadMap;

    private Function<Sector, Shape> roadShapeFunc;

    private Function<City, Set<SimpleLot>> lotGenerator;

    private Function<Vector2i, Integer> heightMap;

    /**
     * @param seed the seed value
     */
    public WorldFacade(final String seed) {
        
        final SimplexNoise terrainHeight = new SimplexNoise(seed.hashCode());

        heightMap = new Function<Vector2i, Integer>() {

            @Override
            public Integer apply(Vector2i pos) {
                return (int) Math.min(12, Math.max(1, (5 + terrainHeight.noise(pos.x / 155d, pos.y / 155d) * 15d)));
            }
            
        };
        
        int minCitiesPerSector = 1;
        int maxCitiesPerSector = 2;
        int minSize = 50;
        int maxSize = 250;
        
        cityMap = new CityPlacerRandom(seed, minCitiesPerSector, maxCitiesPerSector, minSize, maxSize);
        cityMap = CachingFunction.wrap(cityMap);
        
        double maxDist = 0.75;
        connectedCities = new CityConnector(cityMap, maxDist);
        connectedCities = CachingFunction.wrap(connectedCities);

        junctions = new Function<Point2d, Junction>() {

            @Override
            public Junction apply(Point2d input) {
                return new Junction(input);
            }
            
        };
        junctions = CachingFunction.wrap(junctions);
        
        sectorConnections = new SectorConnector(cityMap, connectedCities);
        sectorConnections = CachingFunction.wrap(sectorConnections);

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
        
        final Function<UnorderedPair<City>, Road> cachedRoadgen = CachingFunction.wrap(rg);

        roadMap = new Function<Sector, Set<Road>>() {

            @Override
            public Set<Road> apply(Sector sector) {
                Set<Road> allRoads = Sets.newHashSet();
                
                Set<UnorderedPair<City>> localConns = sectorConnections.apply(sector);
                Set<UnorderedPair<City>> allConns = Sets.newHashSet(localConns);
                
                // add all neighbors, because their roads might be passing through
                for (Orientation dir : Sector.Orientation.values()) {
                    Sector neighbor = sector.getNeighbor(dir);

                    allConns.addAll(sectorConnections.apply(neighbor));
                }

                for (UnorderedPair<City> conn : allConns) {
                    Road road = cachedRoadgen.apply(conn);
                    allRoads.add(road);
                }

                return allRoads;
            }
        };
        
        roadMap = CachingFunction.wrap(roadMap);

        roadShapeFunc = new RoadShapeGenerator(roadMap);
        roadShapeFunc = CachingFunction.wrap(roadShapeFunc);
        
        lotGenerator = new LotGeneratorRandom(seed, roadShapeFunc, heightMap);
        lotGenerator = CachingFunction.wrap(lotGenerator);
    }

    /**
     * @return the terrain height map function
     */
    public Function<Vector2i, Integer> getHeightMap() {
        return this.heightMap;
    }

    /**
     * @param sector the sector
     * @return a shape that describes the area of all roads
     */
    public Shape getRoadArea(Sector sector) {
        return roadShapeFunc.apply(sector);
    }

    /**
     * @param sector the sector
     * @return all cities in that sector
     */
    public Set<City> getCities(Sector sector) {
        return cityMap.apply(sector);
    }

    /**
     * @param city the city
     * @return all lots that are part of the city
     */
    public Set<SimpleLot> getLots(City city) {
        return lotGenerator.apply(city);
    }
}
