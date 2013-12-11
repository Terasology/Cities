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

package org.terasology.world.generator.city;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Point2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.common.CachingFunction;
import org.terasology.common.UnorderedPair;
import org.terasology.core.world.generator.chunkGenerators.FlatTerrainGenerator;
import org.terasology.math.Vector2i;
import org.terasology.math.Vector3i;
import org.terasology.utilities.random.FastRandom;
import org.terasology.world.block.Block;
import org.terasology.world.chunks.Chunk;
import org.terasology.world.generator.city.def.CityConnector;
import org.terasology.world.generator.city.def.CityPlacerRandom;
import org.terasology.world.generator.city.def.LotGeneratorRandom;
import org.terasology.world.generator.city.def.RoadGeneratorSimple;
import org.terasology.world.generator.city.def.RoadModifierRandom;
import org.terasology.world.generator.city.model.City;
import org.terasology.world.generator.city.model.Junction;
import org.terasology.world.generator.city.model.Lot;
import org.terasology.world.generator.city.model.Road;
import org.terasology.world.generator.city.model.Sector;
import org.terasology.world.generator.city.model.Sector.Orientation;
import org.terasology.world.generator.city.model.Sectors;
import org.terasology.world.generator.city.raster.CityRasterizerSimple;
import org.terasology.world.generator.city.raster.LotRenderer;
import org.terasology.world.generator.city.raster.RoadRasterizerSpline;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Sets;

/**
 * Uses world generation code to draw on a swing canvas
 * @author Martin Steiger
 */
public class SwingRasterizer {

    private static final Logger logger = LoggerFactory.getLogger(SwingRasterizer.class);
    
    Function<Sector, Set<City>> cityMap;

    Function<City, Set<City>> connectedCities;

    private Function<Sector, Set<UnorderedPair<City>>> sectorConnections;

    private Function<Point2d, Junction> junctions;

    private Function<Sector, Set<Road>> roadMap;

    // HACK: compute the height properly!
    private Function<? super Vector2i, Integer> heightMap = Functions.constant(FlatTerrainGenerator.DEFAULT_HEIGHT);
    
    private Function<String, Block> blockType = new BlockTypeFunction();
    
    private Map<Sector, Area> roadAreaCache = new HashMap<>();

    private String seed;

    /**
     * @param seed the seed value
     */
    public SwingRasterizer(String seed) {
        int minCitiesPerSector = 1;
        int maxCitiesPerSector = 3;
        int minSize = 20;
        int maxSize = 100;
        
        this.seed = seed;
        
        cityMap = new CityPlacerRandom(seed, minCitiesPerSector, maxCitiesPerSector, minSize, maxSize);
        cityMap = CachingFunction.wrap(cityMap);
        
        double maxDist = 0.8;
        connectedCities = new CityConnector(cityMap, maxDist);
        connectedCities = CachingFunction.wrap(connectedCities);

        junctions = new Function<Point2d, Junction>() {

            @Override
            public Junction apply(Point2d input) {
                return new Junction(input);
            }
            
        };
        junctions = CachingFunction.wrap(junctions);
        
        sectorConnections = new Function<Sector, Set<UnorderedPair<City>>>() {
            @Override
            public Set<UnorderedPair<City>> apply(Sector sector) {
                Set<City> cities = Sets.newHashSet(cityMap.apply(sector));

                Set<UnorderedPair<City>> connections = Sets.newHashSet();
                
                for (City city : cities) {
                    Set<City> conn = connectedCities.apply(city);
                    
                    for (City other : conn) {
                        connections.add(new UnorderedPair<City>(city, other));
                    }
                }
                
                return connections;
            }
        };
        
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
    }

    /**
     * @param chunk the chunk to update
     */
    public void writeChunk(Chunk chunk) {
        Vector3i chunkPos = chunk.getBlockWorldPos(new Vector3i(0, 0, 0));
        
        int wx = chunkPos.x;
        int wz = chunkPos.z;
        int sx = (int) Math.floor((double) wx / Sector.SIZE);
        int sz = (int) Math.floor((double) wz / Sector.SIZE);
        int chunkSizeX = chunk.getChunkSizeX();
        int chunkSizeZ = chunk.getChunkSizeZ();

        Sector sector = Sectors.getSector(new Vector2i(sx, sz));
        
        Set<Road> roads = roadMap.apply(sector);

        Area roadArea = roadAreaCache.get(sector);

        if (roadArea == null) {
            RoadRasterizerSpline rr = new RoadRasterizerSpline();
            roadArea = rr.getRoadArea(sector, roads);
            roadAreaCache.put(sector, roadArea);
        }

        Rectangle2D chunkRect = new Rectangle2D.Double(wx, wz, chunkSizeX, chunkSizeZ);

        if (!roadArea.intersects(chunkRect)) {
            return;
        }
        
        for (int z = 0; z < chunkSizeZ; z++) {
            for (int x = 0; x < chunkSizeX; x++) {
            
                if (roadArea.contains(wx + x, wz + z)) {
                    int y = heightMap.apply(new Vector2i(wx + x, wz + z));
                    Vector3i pos = new Vector3i(x, y, z);
                    Block block = blockType.apply(BlockTypes.ROAD_SURFACE);
                    chunk.setBlock(pos, block);
                }
            }
        }
    }
    
//    private void generateSectorChunks(Sector sector) {
//        
//         LotGeneratorRandom lgr = new LotGeneratorRandom(seed);
//        
//        Set<City> cities = Sets.newHashSet(cityMap.apply(sector));
//        
//        // add all neighbors, because their cities might reach into this sector
//        for (Orientation dir : Sector.Orientation.values()) {
//            Sector neighbor = sector.getNeighbor(dir);
//
//            cities.addAll(cityMap.apply(neighbor));
//        }
//        
//        for (City city : cities) {
//            Set<Lot> lots = lgr.createLots(city, new Area());
//            
//        }
//
//    }

    /**
     * @param g the graphics object
     * @param sector the sector to render
     */
    public void rasterizeSector(Graphics2D g, Sector sector) {

        drawNoiseBackground(g, sector);

        Set<Road> roads = roadMap.apply(sector);

        Area roadArea = roadAreaCache.get(sector);
        
        RoadRasterizerSpline rr = new RoadRasterizerSpline();

        if (roadArea == null) {
            roadArea = rr.getRoadArea(sector, roads);
            roadAreaCache.put(sector, roadArea);
        }
        
        logger.debug("Drawing {} roads for {}", roads.size(), sector);

        rr.rasterRoadArea(g, roadArea);

        drawCities(g, roadArea, sector);
        
        
        drawFrame(g, sector);
        drawSectorText(g, sector);
    }
    
    private void drawCities(Graphics2D g, Area roadArea, Sector sector) {
        CityRasterizerSimple sr = new CityRasterizerSimple(); 
        LotGeneratorRandom lgr = new LotGeneratorRandom(seed);
        LotRenderer lr = new LotRenderer();
        
        Set<City> cities = Sets.newHashSet(cityMap.apply(sector));
        
        // add all neighbors, because their cities might reach into this sector
        for (Orientation dir : Sector.Orientation.values()) {
            Sector neighbor = sector.getNeighbor(dir);

            cities.addAll(cityMap.apply(neighbor));
        }
        
        for (City city : cities) {
            Set<Lot> lots = lgr.createLots(city, roadArea);
            
            lr.rasterLots(g, lots);
            sr.rasterCity(g, city);
            drawCityName(g, city);
        }
    }

    private void drawCityName(Graphics2D g, City ci) {
        String text = ci.toString();
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Color.MAGENTA);
        int width = fm.stringWidth(text);
        int cx = (int) ((ci.getPos().x) * Sector.SIZE);
        int cz = (int) ((ci.getPos().y) * Sector.SIZE);
        g.drawString(text, cx - width / 2, cz + fm.getAscent() + 5);
    }

    private void drawNoiseBackground(Graphics2D g, Sector sector) {
        FastRandom random = new FastRandom(sector.hashCode());
        BufferedImage img = new BufferedImage(Sector.SIZE, Sector.SIZE, BufferedImage.TYPE_INT_ARGB);
        
        for (int y = 0; y < Sector.SIZE; y++) {
            for (int x = 0; x < Sector.SIZE; x++) {
                int v = 235 + random.nextInt(20);
                Color c = new Color(v, v, v);
                img.setRGB(x, y, c.getRGB());
            }
        }

        int offX = Sector.SIZE * sector.getCoords().x;
        int offZ = Sector.SIZE * sector.getCoords().y;

        g.drawImage(img, offX, offZ, null);
    }
    
   private void drawSectorText(Graphics2D g, Sector sector) {
       int offX = Sector.SIZE * sector.getCoords().x;
       int offZ = Sector.SIZE * sector.getCoords().y;

       g.setColor(Color.BLUE);
       Font oldFont = g.getFont();
       g.setFont(oldFont.deriveFont(10f));
       g.drawString(sector.toString(), offX + 5, offZ + g.getFontMetrics().getAscent());
       g.setFont(oldFont);
   }
   
   
    private void drawFrame(Graphics2D g, Sector sector) {
        
        int offX = Sector.SIZE * sector.getCoords().x;
        int offZ = Sector.SIZE * sector.getCoords().y;

        g.setColor(Color.BLUE);
        g.setStroke(new BasicStroke(0.0f));
        g.drawRect(offX, offZ, Sector.SIZE, Sector.SIZE);
    }
}
