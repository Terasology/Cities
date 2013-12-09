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
import java.awt.image.BufferedImage;
import java.util.Set;

import javax.vecmath.Point2d;

import org.terasology.common.CachingFunction;
import org.terasology.common.UnorderedPair;
import org.terasology.utilities.random.FastRandom;
import org.terasology.world.generator.city.def.CityConnector;
import org.terasology.world.generator.city.def.CityPlacerRandom;
import org.terasology.world.generator.city.def.RoadGeneratorSimple;
import org.terasology.world.generator.city.def.RoadModifierRandom;
import org.terasology.world.generator.city.model.City;
import org.terasology.world.generator.city.model.Junction;
import org.terasology.world.generator.city.model.Road;
import org.terasology.world.generator.city.model.Sector;
import org.terasology.world.generator.city.model.Sector.Orientation;
import org.terasology.world.generator.city.raster.CityRasterizerSimple;
import org.terasology.world.generator.city.raster.RoadRasterizerSimple;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

/**
 * Uses world generation code to draw on a swing canvas
 * @author Martin Steiger
 */
public class SwingRasterizer {

    Function<Sector, Set<City>> cityMap;

    Function<City, Set<City>> connectedCities;

    private Function<Sector, Set<UnorderedPair<City>>> sectorConnections;

    private Function<Point2d, Junction> junctions;

    private Function<Sector, Set<Road>> roadMap;

    /**
     * @param seed the seed value
     */
    public SwingRasterizer(String seed) {
        int minCitiesPerSector = 1;
        int maxCitiesPerSector = 3;
        int minSize = 10;
        int maxSize = 100;
        
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
            private RoadModifierRandom rmr = new RoadModifierRandom(0.02);

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
     * @param g the graphics object
     * @param sector the sector to render
     */
    public void rasterizeSector(Graphics2D g, Sector sector) {

        drawNoiseBackground(g, sector);

        Set<Road> roads = roadMap.apply(sector);
        
        RoadRasterizerSimple rr = new RoadRasterizerSimple();
//      RoadRasterizerSpline rr = new RoadRasterizerSpline();
        rr.rasterRoads(g, roads);

        CityRasterizerSimple sr = new CityRasterizerSimple(); 
        
        
        Set<City> cis = cityMap.apply(sector);
        for (City ci : cis) {
            sr.rasterCity(g, ci);
            drawCityName(g, ci);
        }
        
        drawFrame(g, sector);
        drawSectorText(g, sector);
    }
    
    private void drawCityName(Graphics2D g, City ci) {
        String text = ci.toString();
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Color.BLUE);
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
