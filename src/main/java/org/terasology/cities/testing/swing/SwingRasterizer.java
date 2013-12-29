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

package org.terasology.cities.testing.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Set;

import org.terasology.cities.BlockTypes;
import org.terasology.cities.WorldFacade;
import org.terasology.cities.common.Orientation;
import org.terasology.cities.model.City;
import org.terasology.cities.model.Lot;
import org.terasology.cities.model.Sector;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.TerrainInfo;
import org.terasology.cities.raster.standard.CityRasterizer;
import org.terasology.cities.raster.standard.RoadRasterizer;
import org.terasology.cities.terrain.CachingHeightMap;
import org.terasology.cities.terrain.NoiseHeightMap;
import org.terasology.math.TeraMath;
import org.terasology.world.chunks.ChunkConstants;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Uses world generation code to draw on a swing canvas
 * @author Martin Steiger
 */
public class SwingRasterizer {

    private final WorldFacade facade;
    private final NoiseHeightMap heightMap;
    
    private final Map<BlockTypes, Color> themeMap = Maps.newConcurrentMap();
    
    /**
     * @param seed the seed value
     */
    public SwingRasterizer(String seed) {
        heightMap = new NoiseHeightMap();
        heightMap.setSeed(seed);
        
        facade = new WorldFacade(seed, heightMap);
        
        themeMap.put(BlockTypes.AIR, new Color(0, 0, 0, 0));
        themeMap.put(BlockTypes.ROAD_SURFACE, new Color(64, 64, 64));
        themeMap.put(BlockTypes.LOT_EMPTY, new Color(224, 224, 64));
        themeMap.put(BlockTypes.BUILDING_WALL, new Color(158, 158, 158));
        themeMap.put(BlockTypes.BUILDING_FLOOR, new Color(100, 100, 100));
        themeMap.put(BlockTypes.BUILDING_FOUNDATION, new Color(90, 60, 60));
        themeMap.put(BlockTypes.ROOF_FLAT, new Color(255, 60, 60));
        themeMap.put(BlockTypes.ROOF_HIP, new Color(255, 60, 60));
        themeMap.put(BlockTypes.ROOF_SADDLE, new Color(224, 120, 100));
        themeMap.put(BlockTypes.ROOF_DOME, new Color(160, 190, 190));
        themeMap.put(BlockTypes.ROOF_GABLE, new Color(180, 120, 100));

        themeMap.put(BlockTypes.TOWER_WALL, new Color(200, 100, 200));        
    }

    /**
     * @param g the graphics object
     * @param sector the sector to render
     */
    public void rasterizeSector(Graphics2D g, Sector sector) {

        drawNoiseBackground(g, sector);

        boolean drawFast = false;
        
        if (drawFast) {
            drawRoads(g, sector);
            drawCities(g, sector);
        } else {
            drawAccurately(g, sector);
        }
        
        drawFrame(g, sector);
        drawSectorText(g, sector);
    }

    private void drawAccurately(Graphics2D g, Sector sector) {
        int chunkSizeX = ChunkConstants.SIZE_X * 4;
        int chunkSizeZ = ChunkConstants.SIZE_Z * 4;
        
        int chunksX = Sector.SIZE / chunkSizeX;
        int chunksZ = Sector.SIZE / chunkSizeZ;
        
        Function<BlockTypes, Color> colorFunc = new Function<BlockTypes, Color>() {
            
            @Override
            public Color apply(BlockTypes input) {
                Color color = themeMap.get(input);
                
                if (color == null) {
                    color = Color.GRAY;
                }
                
                return color;
            }
        };
        
        for (int cz = 0; cz < chunksZ; cz++) {
            for (int cx = 0; cx < chunksX; cx++) {
                int wx = sector.getCoords().x * Sector.SIZE + cx * chunkSizeX;
                int wz = sector.getCoords().y * Sector.SIZE + cz * chunkSizeZ;
                BufferedImage image = new BufferedImage(chunkSizeX, chunkSizeZ, BufferedImage.TYPE_INT_ARGB);
                Brush brush = new SwingBrush(wx, wz, image, colorFunc);

                if (g.hitClip(wx, wz, image.getWidth(), image.getHeight())) {

                    CachingHeightMap cachedHm = new CachingHeightMap(brush.getAffectedArea(), heightMap);
                    TerrainInfo ti = new TerrainInfo(cachedHm);

                    drawCities(sector, ti, brush);
                    drawRoads(sector, ti, brush);

                    int ix = wx;
                    int iy = wz;
                    g.drawImage(image, ix, iy, null);
                }
            }
        }
        
        for (City city : facade.getCities(sector)) {
            drawCityName(g, city);
        }

    }
    
    private void drawRoads(Sector sector, TerrainInfo ti, Brush brush) {
        Shape roadArea = facade.getRoadArea(sector);
    
        RoadRasterizer rr = new RoadRasterizer();
        rr.raster(brush, ti, roadArea);
    }
    
    private void drawCities(Sector sector, TerrainInfo ti, Brush brush) {
        Set<City> cities = Sets.newHashSet(facade.getCities(sector));
    
        for (Orientation dir : Orientation.values()) {
            cities.addAll(facade.getCities(sector.getNeighbor(dir)));
        }
    
        CityRasterizer cr = new CityRasterizer();
    
        for (City city : cities) {
            cr.raster(brush, ti, city);
        }
    }
    
    private void drawRoads(Graphics2D g, Sector sector) {
        
        Shape roadArea = facade.getRoadArea(sector);

        g.setStroke(new BasicStroke());
        g.setColor(new Color(224, 96, 32));
        g.fill(roadArea);
        g.setColor(Color.BLACK);
        g.draw(roadArea);
    }

    private void drawCities(Graphics2D g, Sector sector) {
        CityRasterizerSimple sr = new CityRasterizerSimple(); 
        LotRenderer lr = new LotRenderer();
        
        Set<City> cities = Sets.newHashSet(facade.getCities(sector));
        
        // add all neighbors, because their cities might reach into this sector
        for (Orientation dir : Orientation.values()) {
            Sector neighbor = sector.getNeighbor(dir);

            cities.addAll(facade.getCities(neighbor));
        }
        
        for (City city : cities) {
            Set<Lot> lots = city.getLots();
            
            lr.rasterLots(g, lots);
            sr.rasterCity(g, city);
            drawCityName(g, city);
        }
    }

    private void drawCityName(Graphics2D g, City ci) {
        String text = ci.toString();

        int cx = ci.getPos().x;
        int cz = ci.getPos().y;

        Font font = g.getFont();
        FontMetrics fm = g.getFontMetrics(font);
        int width = fm.stringWidth(text);

        g.setColor(Color.BLACK);
        g.drawString(text, cx - width / 2, cz + (float) ci.getDiameter() * 0.5f + 6f);
    }

    private void drawNoiseBackground(Graphics2D g, Sector sector) {
        int scale = 4;
        int size = Sector.SIZE / scale;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int gx = sector.getCoords().x * Sector.SIZE + x * scale;
                int gz = sector.getCoords().y * Sector.SIZE + y * scale;
                int height = heightMap.apply(gx, gz);
                int b = TeraMath.clamp(185 + height * 5, 0, 255);

                Color c = new Color(b, b, b);
                img.setRGB(x, y, c.getRGB());
            }
        }

        int offX = Sector.SIZE * sector.getCoords().x;
        int offZ = Sector.SIZE * sector.getCoords().y;

        g.drawImage(img, offX, offZ, Sector.SIZE, Sector.SIZE, null);
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
        g.setStroke(new BasicStroke());
    }
}
