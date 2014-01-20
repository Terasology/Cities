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

package org.terasology.cities.swing.draw;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Set;

import org.terasology.cities.BlockTypes;
import org.terasology.cities.CityWorldConfig;
import org.terasology.cities.WorldFacade;
import org.terasology.cities.common.Orientation;
import org.terasology.cities.contour.Contour;
import org.terasology.cities.model.City;
import org.terasology.cities.model.Lake;
import org.terasology.cities.model.Lot;
import org.terasology.cities.model.Sector;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.RasterRegistry;
import org.terasology.cities.raster.TerrainInfo;
import org.terasology.cities.raster.standard.RoadRasterizer;
import org.terasology.cities.raster.standard.StandardRegistry;
import org.terasology.cities.terrain.HeightMap;
import org.terasology.cities.terrain.HeightMaps;
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
        
        CityWorldConfig config = new CityWorldConfig();
        facade = new WorldFacade(seed, heightMap, config);
        
        themeMap.put(BlockTypes.AIR, new Color(0, 0, 0, 0));
        themeMap.put(BlockTypes.ROAD_SURFACE, new Color(160, 40, 40));
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

        boolean drawFast = false;
        
        if (drawFast) {
            drawNoiseBackgroundFast(g, sector);
            drawRoads(g, sector);
            drawCities(g, sector);
        } else {
            drawAccurately(g, sector);
        }
        
        drawLakes(g, sector);
        drawFrame(g, sector);
        drawSectorText(g, sector);
    }

    private void drawLakes(Graphics2D g, Sector sector) {
        
        int scale = 8;

        Set<Lake> lakes = facade.getLakes(sector);

        Graphics2D gc = (Graphics2D) g.create();
        gc.scale(scale, scale);
        gc.translate(0.5, 0.5);
        gc.setStroke(new BasicStroke(2f / scale));
        gc.setFont(gc.getFont().deriveFont(2f));
        
        for (Lake l : lakes) {
            
            Contour cont = l.getContour();
            for (Point p : cont.getSimplifiedCurve()) {
                double r = 0.2;
                gc.draw(new Ellipse2D.Double(p.x - r, p.y - r, 2 * r, 2 * r));
            }

            Polygon poly = cont.getPolygon();
            gc.draw(poly);
            
            int cx = (int) poly.getBounds().getCenterX();
            int cy = (int) poly.getBounds().getCenterY();
            
            FontMetrics fm = gc.getFontMetrics();
            int lw = fm.stringWidth(l.getName());
            int lh = fm.getHeight();
            gc.drawString(l.getName(), cx - lw / 2, cy - lh / 2);
        }
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

                if (g.hitClip(wx, wz, chunkSizeX, chunkSizeZ)) {

                    BufferedImage image = new BufferedImage(chunkSizeX, chunkSizeZ, BufferedImage.TYPE_INT_ARGB);
                    Brush brush = new SwingBrush(wx, wz, image, colorFunc);

                    HeightMap cachedHm = HeightMaps.caching(heightMap, brush.getAffectedArea(), 8);
                    TerrainInfo ti = new TerrainInfo(cachedHm);

                    drawBackground(image, wx, wz, ti);
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
    
        RasterRegistry registry = StandardRegistry.getInstance();

        for (City city : cities) {
            registry.rasterize(brush, ti, city);
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
        g.drawString(text, cx - width / 2, cz + (float) ci.getDiameter() * 0.5f + 10f);
    }

    private void drawNoiseBackgroundFast(Graphics2D g, Sector sector) {
        int scale = 4;
        int maxHeight = 20;
        int size = Sector.SIZE / scale;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int gx = sector.getCoords().x * Sector.SIZE + x * scale;
                int gz = sector.getCoords().y * Sector.SIZE + y * scale;
                int height = heightMap.apply(gx, gz);
                int b = TeraMath.clamp(255 - (maxHeight - height) * 5, 0, 255);

                Color c;
                if (height <= 2) {
                    c = Color.BLUE; 
                } else {
                    c = new Color(b, b, b);
                }
                
                img.setRGB(x, y, c.getRGB());
            }
        }

        int offX = Sector.SIZE * sector.getCoords().x;
        int offZ = Sector.SIZE * sector.getCoords().y;

        g.drawImage(img, offX, offZ, Sector.SIZE, Sector.SIZE, null);
    }
    
    private void drawBackground(BufferedImage image, int wx, int wz, TerrainInfo ti) {
        int width = image.getWidth();
        int height = image.getHeight();
        int maxHeight = 20;
        
        for (int z = 0; z < height; z++) {
            for (int x = 0; x < width; x++) {
                int gx = wx + x;
                int gz = wz + z;
                int y = ti.getHeightMap().apply(gx, gz);
                int b = TeraMath.clamp(255 - (maxHeight - y) * 5, 0, 255);

                Color c;
                if (y <= 2) {
                    c = Color.BLUE; 
                } else {
                    c = new Color(b, b, b);
                }
                
                image.setRGB(x, z, c.getRGB());
            }
        }
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
