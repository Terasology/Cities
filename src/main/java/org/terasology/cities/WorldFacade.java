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

package org.terasology.cities;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Set;

import javax.vecmath.Point2i;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cities.common.CachingFunction;
import org.terasology.cities.common.Orientation;
import org.terasology.cities.common.Profiler;
import org.terasology.cities.common.UnorderedPair;
import org.terasology.cities.generator.CityConnector;
import org.terasology.cities.generator.CityPlacerRandom;
import org.terasology.cities.generator.DefaultTownWallGenerator;
import org.terasology.cities.generator.LotGeneratorRandom;
import org.terasology.cities.generator.RoadGeneratorSimple;
import org.terasology.cities.generator.RoadModifierRandom;
import org.terasology.cities.generator.RoadShapeGenerator;
import org.terasology.cities.generator.SimpleChurchGenerator;
import org.terasology.cities.generator.SimpleFenceGenerator;
import org.terasology.cities.generator.SimpleHousingGenerator;
import org.terasology.cities.generator.TownWallShapeGenerator;
import org.terasology.cities.model.City;
import org.terasology.cities.model.Junction;
import org.terasology.cities.model.MedievalTown;
import org.terasology.cities.model.Road;
import org.terasology.cities.model.Sector;
import org.terasology.cities.model.SimpleBuilding;
import org.terasology.cities.model.SimpleChurch;
import org.terasology.cities.model.SimpleFence;
import org.terasology.cities.model.SimpleLot;
import org.terasology.cities.model.TownWall;
import org.terasology.cities.terrain.HeightMap;
import org.terasology.cities.terrain.HeightMaps;
import org.terasology.engine.CoreRegistry;
import org.terasology.math.TeraMath;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Sets;

/**
 * Provides many different getters to rasterize a world
 * @author Martin Steiger
 */
public class WorldFacade {

    private static final Logger logger = LoggerFactory.getLogger(WorldFacade.class);
    
    private CachingFunction<Sector, Set<City>> decoratedCities;

    private Function<City, Set<City>> connectedCities;

    private Function<Sector, Set<UnorderedPair<City>>> sectorConnections;

    private Function<Point2i, Junction> junctions;

    private Function<Sector, Set<Road>> roadMap;

    private Function<Sector, Shape> roadShapeFunc;

    /**
     * @param seed the seed value
     * @param heightMap the height map to use
     * @param config the world configuration
     */
    public WorldFacade(String seed, final HeightMap heightMap, final CityWorldConfig config) {

        junctions = new Function<Point2i, Junction>() {

            @Override
            public Junction apply(Point2i input) {
                return new Junction(input);
            }
            
        };
        junctions = CachingFunction.wrap(junctions);
        
        int minCitiesPerSector = config.getMinCitiesPerSector();
        int maxCitiesPerSector = config.getMaxCitiesPerSector();
        int minSize = config.getMinCityRadius();
        int maxSize = config.getMaxCityRadius();
        
        AreaInfo globalAreaInfo = new AreaInfo(config, heightMap);
        
        Function<? super Sector, AreaInfo> sectorInfos = Functions.constant(globalAreaInfo);
        CityPlacerRandom cpr = new CityPlacerRandom(seed, sectorInfos, minCitiesPerSector, maxCitiesPerSector, minSize, maxSize);
        final Function<Sector, Set<City>> cityMap = CachingFunction.wrap(cpr);
        
        double maxDist = config.getMaxConnectedCitiesDistance();
        connectedCities = new CityConnector(cityMap, maxDist);
        connectedCities = CachingFunction.wrap(connectedCities);
        
        sectorConnections = new SectorConnector(cityMap, connectedCities);
        sectorConnections = CachingFunction.wrap(sectorConnections);

        Function<UnorderedPair<City>, Road> rg = new Function<UnorderedPair<City>, Road>() {
            private RoadGeneratorSimple rgs = new RoadGeneratorSimple(junctions);
            private RoadModifierRandom rmr = new RoadModifierRandom(10);

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
                for (Orientation dir : Orientation.values()) {
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
        
        final DefaultTownWallGenerator twg = new DefaultTownWallGenerator(seed, heightMap);
        final LotGeneratorRandom housingLotGenerator = new LotGeneratorRandom(seed);
        final LotGeneratorRandom churchLotGenerator = new LotGeneratorRandom(seed, 25d, 40d, 1, 100);
        final SimpleHousingGenerator blgGenerator = new SimpleHousingGenerator(seed, heightMap);
        final SimpleFenceGenerator sfg = new SimpleFenceGenerator(seed);
        final SimpleChurchGenerator sacg = new SimpleChurchGenerator(seed, heightMap);

        decoratedCities = CachingFunction.wrap(new Function<Sector, Set<City>>() {
            
            @Override
            public Set<City> apply(Sector input) {
                
                if (logger.isInfoEnabled()) {
                    Profiler.start(input);
                }

                if (logger.isInfoEnabled()) {
                    Profiler.start(input + "sites");
                }
                
                Set<City> cities = cityMap.apply(input);

                if (logger.isInfoEnabled()) {
                    String timeStr = Profiler.getAsStringAndStop(input + "sites");
                    logger.info("Generated settlement sites for {} in {}", input, timeStr);
                }

                if (logger.isInfoEnabled()) {
                    Profiler.start(input + "roads");
                }

                Shape roadShape = roadShapeFunc.apply(input);

                if (logger.isInfoEnabled()) {
                    String timeStr = Profiler.getAsStringAndStop(input + "roads");
                    logger.info("Generated roads for {} in {}", input, timeStr);
                }
                
                for (City city : cities) {
                    
                    if (logger.isInfoEnabled()) {
                        Profiler.start(city);
                    }
                    
                    int minX = city.getPos().x - TeraMath.ceilToInt(city.getDiameter() * 0.5);
                    int maxX = city.getPos().x + TeraMath.ceilToInt(city.getDiameter() * 0.5);
                    int minZ = city.getPos().y - TeraMath.ceilToInt(city.getDiameter() * 0.5);
                    int maxZ = city.getPos().y + TeraMath.ceilToInt(city.getDiameter() * 0.5);
                    
                    Rectangle cityArea = new Rectangle(minX, minZ, maxX - minX, maxZ - minZ);
                    HeightMap cityAreaHeightMap = HeightMaps.caching(heightMap, cityArea, 4);

                    AreaInfo si = new AreaInfo(config, cityAreaHeightMap); 
                    si.addBlockedArea(roadShape);
                    
                    if (city instanceof MedievalTown) {
                        MedievalTown town = (MedievalTown) city;
                        TownWall tw = twg.generate(city, si);
                        town.setTownWall(tw);

                        TownWallShapeGenerator twsg = new TownWallShapeGenerator();
                        Shape townWallShape = twsg.computeShape(tw);
                        si.addBlockedArea(townWallShape);
                    }

                    if (city instanceof MedievalTown) {
                        Set<SimpleLot> lots = churchLotGenerator.generate(city, si);
                        if (!lots.isEmpty()) {
                            SimpleLot lot = lots.iterator().next();
                            SimpleChurch church = sacg.generate(lot);
                            lot.addBuilding(church);
                            city.add(lot);
                        }
                    }
                    
                    Set<SimpleLot> lots = housingLotGenerator.generate(city, si);
                    
                    for (SimpleLot lot : lots) {
                        city.add(lot);
                        
                        for (SimpleBuilding bldg : blgGenerator.apply(lot)) {
                            lot.addBuilding(bldg);
                            SimpleFence fence = sfg.createFence(city, lot.getShape());
                            lot.setFence(fence);
                        }
                    }
                    
                    if (logger.isInfoEnabled()) {
                        String timeStr = Profiler.getAsStringAndStop(city);
                        logger.info("Generated city '{}' in {} in {}", city, input, timeStr);
                    }                    
                }
                
                if (logger.isInfoEnabled()) {
                    String timeStr = Profiler.getAsStringAndStop(input);
                    logger.info("Generated {} .. in {}", input, timeStr);
                }

                return cities;
            }
        });
        
        // this required by WorldEventReceiver
        CoreRegistry.putPermanently(WorldFacade.class, this);
    }
    
    /**
     * Clears the caches
     */
    public void expungeCache() {
        decoratedCities.invalidateAll();
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
        return decoratedCities.apply(sector);
    }

}
