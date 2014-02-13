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

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.vecmath.Point2i;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cities.common.CachingFunction;
import org.terasology.cities.common.Orientation;
import org.terasology.cities.common.UnorderedPair;
import org.terasology.cities.contour.Contour;
import org.terasology.cities.contour.ContourTracer;
import org.terasology.cities.generator.DefaultTownWallGenerator;
import org.terasology.cities.generator.LotGeneratorRandom;
import org.terasology.cities.generator.RoadGeneratorSimple;
import org.terasology.cities.generator.RoadModifierRandom;
import org.terasology.cities.generator.RoadShapeGenerator;
import org.terasology.cities.generator.SimpleChurchGenerator;
import org.terasology.cities.generator.SimpleFenceGenerator;
import org.terasology.cities.generator.SimpleHousingGenerator;
import org.terasology.cities.generator.SiteConnector;
import org.terasology.cities.generator.SiteFinderRandom;
import org.terasology.cities.generator.TownWallShapeGenerator;
import org.terasology.cities.heightmap.HeightMap;
import org.terasology.cities.heightmap.HeightMaps;
import org.terasology.cities.model.City;
import org.terasology.cities.model.Junction;
import org.terasology.cities.model.Lake;
import org.terasology.cities.model.MedievalTown;
import org.terasology.cities.model.NamedArea;
import org.terasology.cities.model.Road;
import org.terasology.cities.model.Sector;
import org.terasology.cities.model.SimpleFence;
import org.terasology.cities.model.SimpleLot;
import org.terasology.cities.model.Site;
import org.terasology.cities.model.bldg.SimpleBuilding;
import org.terasology.cities.model.bldg.SimpleChurch;
import org.terasology.cities.model.bldg.TownWall;
import org.terasology.namegenerator.town.DebugTownTheme;
import org.terasology.namegenerator.town.TownAffinityVector;
import org.terasology.namegenerator.town.TownNameProvider;
import org.terasology.namegenerator.waters.DebugWaterTheme;
import org.terasology.namegenerator.waters.WaterNameProvider;
import org.terasology.registry.CoreRegistry;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Objects;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;

/**
 * Provides many different getters to rasterize a world
 * @author Martin Steiger
 */
public class WorldFacade {

    private static final Logger logger = LoggerFactory.getLogger(WorldFacade.class);
    
    private CachingFunction<Sector, Set<City>> decoratedCities;

    private Function<Site, Set<Site>> connectedCities;

    private Function<Sector, Set<UnorderedPair<Site>>> sectorConnections;

    private Function<Point2i, Junction> junctions;

    private Function<Sector, Set<Road>> roadMap;

    private Function<Sector, Shape> roadShapeFunc;

    private Function<Sector, Set<Lake>> lakeMap;

    /**
     * @param seed the seed value
     * @param heightMap the height map to use
     * @param config the world configuration
     */
    public WorldFacade(final String seed, final HeightMap heightMap, final CityWorldConfig config) {

        junctions = new Function<Point2i, Junction>() {

            @Override
            public Junction apply(Point2i input) {
                return new Junction(input);
            }
            
        };
        junctions = CachingFunction.wrap(junctions);
        
        lakeMap = CachingFunction.wrap(new Function<Sector, Set<Lake>>() {

            @Override
            public Set<Lake> apply(Sector sector) {

                Integer salt = 2354234;
                int ngseed = Objects.hashCode(salt, seed, sector);
                WaterNameProvider ng = new WaterNameProvider(ngseed, new DebugWaterTheme());
                
                int minSize = 16;

                int scale = 8;
                int size = Sector.SIZE / scale;
                HeightMap orgHm = HeightMaps.scalingArea(heightMap, scale);
                Point2i coords = sector.getCoords();
                
                Rectangle sectorRect = new Rectangle(coords.x * size, coords.y * size, size, size);
                ContourTracer ct = new ContourTracer(orgHm, sectorRect, new CityWorldConfig().getSeaLevel());
                
                Set<Lake> lakes = Sets.newHashSet();
                
                for (Contour c : ct.getOuterContours()) {
                    Contour scaledContour = c.scale(scale);
                    Polygon polyLake = scaledContour.getPolygon();

                    if (polyLake.getBounds().width > minSize
                     && polyLake.getBounds().height > minSize) {
                        Lake lake = new Lake(scaledContour, ng.generateName());
                        
                        for (Contour isl : ct.getInnerContours()) {
                            Rectangle bboxIsland = isl.getPolygon().getBounds();
                            
                            if (polyLake.getBounds().contains(bboxIsland)) {
                                if (allInside(polyLake, isl.getPoints())) {
                                    lake.addIsland(isl);
                                }
                            }
                        }
                        
                        lakes.add(lake);
                    }
                }
                
                return lakes;
            }

            private boolean allInside(Polygon polygon, Collection<Point> points) {
                for (Point pt : points) {
                    if (!polygon.contains(pt)) {
                        return false;
                    }
                }
                return true;
            }
        });
        
        int minCitiesPerSector = config.getMinCitiesPerSector();
        int maxCitiesPerSector = config.getMaxCitiesPerSector();
        int minSize = config.getMinCityRadius();
        int maxSize = config.getMaxCityRadius();
        
        AreaInfo globalAreaInfo = new AreaInfo(config, heightMap);
        
        Function<? super Sector, AreaInfo> sectorInfos = Functions.constant(globalAreaInfo);
        SiteFinderRandom cpr = new SiteFinderRandom(seed, sectorInfos, minCitiesPerSector, maxCitiesPerSector, minSize, maxSize);
        final Function<Sector, Set<Site>> siteMap = CachingFunction.wrap(cpr);
        
        double maxDist = config.getMaxConnectedCitiesDistance();
        connectedCities = new SiteConnector(siteMap, maxDist);
        connectedCities = CachingFunction.wrap(connectedCities);
        
        sectorConnections = new SectorConnector(siteMap, connectedCities);
        sectorConnections = CachingFunction.wrap(sectorConnections);

        Function<UnorderedPair<Site>, Road> rg = new Function<UnorderedPair<Site>, Road>() {
            private RoadGeneratorSimple rgs = new RoadGeneratorSimple(junctions);
            private RoadModifierRandom rmr = new RoadModifierRandom(0.5);

            @Override
            public Road apply(UnorderedPair<Site> input) {
                Road road = rgs.apply(input);
                rmr.apply(road);
                return road;
            }
            
        };
        
        final Function<UnorderedPair<Site>, Road> cachedRoadgen = CachingFunction.wrap(rg);

        roadMap = new Function<Sector, Set<Road>>() {

            @Override
            public Set<Road> apply(Sector sector) {
                Set<Road> allRoads = Sets.newHashSet();
                
                Set<UnorderedPair<Site>> localConns = sectorConnections.apply(sector);
                Set<UnorderedPair<Site>> allConns = Sets.newHashSet(localConns);
                Set<Lake> allBlockedAreas = Sets.newHashSet(lakeMap.apply(sector));
                
                // add all neighbors, because their roads might be passing through
                for (Orientation dir : Orientation.values()) {
                    Sector neighbor = sector.getNeighbor(dir);

                    allConns.addAll(sectorConnections.apply(neighbor));
                    allBlockedAreas.addAll(lakeMap.apply(sector));
                }

                for (UnorderedPair<Site> conn : allConns) {
                    Road road = cachedRoadgen.apply(conn);
                    
                    if (!isBlocked(road, lakeMap.apply(sector))) {
                        allRoads.add(road);
                    }
                }

                return allRoads;
            }
            
            public boolean isBlocked(Road road, Set<? extends NamedArea> blockedAreas) {
                for (Point2i pt : road.getPoints()) {
                    Vector2d v = new Vector2d(pt.x, pt.y);
                    for (NamedArea area : blockedAreas) {
                        if (area.contains(v)) {
                            return true;
                        }
                    }
                }

                return false;
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

                int sectorSeed = Objects.hashCode(seed, input);
                TownNameProvider nameGen = new TownNameProvider(sectorSeed, new DebugTownTheme());
                Stopwatch pAll = null;
                Stopwatch pSites = null;
                Stopwatch pRoads = null;

                if (logger.isInfoEnabled()) {
                    pAll = Stopwatch.createStarted();
                }

                if (logger.isInfoEnabled()) {
                    pSites = Stopwatch.createStarted();
                }
                
                Set<Site> sites = siteMap.apply(input);

                if (logger.isInfoEnabled()) {
                    logger.info("Generated settlement sites for {} in {}ms.", input, pSites.elapsed(TimeUnit.MILLISECONDS));
                }

                if (logger.isInfoEnabled()) {
                    pRoads = Stopwatch.createStarted();
                }

                Shape roadShape = roadShapeFunc.apply(input);

                if (logger.isInfoEnabled()) {
                    logger.info("Generated roads for {} in {}ms.", input, pRoads.elapsed(TimeUnit.MILLISECONDS));
                }
                
                Set<City> cities = Sets.newHashSet();
                
                for (Site site : sites) {
                    
                    Stopwatch pSite = null;
                    if (logger.isInfoEnabled()) {
                        pSite = Stopwatch.createStarted();
                    }
                    
                    int minX = site.getPos().x - site.getRadius();
                    int minZ = site.getPos().y - site.getRadius();
                    
                    Rectangle cityArea = new Rectangle(minX, minZ, site.getRadius() * 2, site.getRadius() * 2);
                    HeightMap cityAreaHeightMap = HeightMaps.caching(heightMap, cityArea, 4);

                    AreaInfo si = new AreaInfo(config, cityAreaHeightMap); 
                    si.addBlockedArea(roadShape);
                    
                    String name = nameGen.generateName(TownAffinityVector.create().prefix(0.2).postfix(0.2));
                    MedievalTown town = new MedievalTown(name, site.getPos(), site.getRadius());

                    // add a town wall if radius is larger than 1/4
                    int minRadForTownWall = (config.getMinCityRadius() * 3 + config.getMaxCityRadius()) / 4;
                    
                    if (town.getRadius() > minRadForTownWall) {
                        TownWall tw = twg.generate(town, si);
                        town.setTownWall(tw);
    
                        TownWallShapeGenerator twsg = new TownWallShapeGenerator();
                        Shape townWallShape = twsg.computeShape(tw);
                        si.addBlockedArea(townWallShape);
                    }
                    
                    Set<SimpleLot> churchLots = churchLotGenerator.generate(town, si);
                    if (!churchLots.isEmpty()) {
                        SimpleLot lot = churchLots.iterator().next();
                        SimpleChurch church = sacg.generate(lot);
                        lot.addBuilding(church);
                        town.add(lot);
                    }
                    
                    Set<SimpleLot> housingLots = housingLotGenerator.generate(town, si);
                    
                    for (SimpleLot lot : housingLots) {
                        town.add(lot);
                        
                        for (SimpleBuilding bldg : blgGenerator.apply(lot)) {
                            lot.addBuilding(bldg);
                            SimpleFence fence = sfg.createFence(town, lot.getShape());
                            lot.setFence(fence);
                        }
                    }
                    
                    if (logger.isInfoEnabled()) {
                        logger.info("Generated city '{}' in {} in {}ms.", town, input, pSite.elapsed(TimeUnit.MILLISECONDS));
                    }
                    
                    cities.add(town);
                }
                
                if (logger.isInfoEnabled()) {
                    logger.info("Generated {} .. in {}ms.", input, pAll.elapsed(TimeUnit.MILLISECONDS));
                }

                return cities;
            }
        });
        
        // this required by WorldEventReceiver
        CoreRegistry.put(WorldFacade.class, this);
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
    public Set<Road> getRoads(Sector sector) {
        return roadMap.apply(sector);
    }
    
    /**
     * @param sector the sector
     * @return all cities in that sector
     */
    public Set<City> getCities(Sector sector) {
        return decoratedCities.apply(sector);
    }

    /**
     * @param sector the sector
     * @return a set of all lakes in that sector
     */
    public Set<Lake> getLakes(Sector sector) {
        return lakeMap.apply(sector);
    }
}
