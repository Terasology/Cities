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
import java.awt.image.BufferedImage;
import java.util.Set;

import org.terasology.cities.WorldFacade;
import org.terasology.cities.model.City;
import org.terasology.cities.model.Lot;
import org.terasology.cities.model.Sector;
import org.terasology.cities.model.Sector.Orientation;
import org.terasology.cities.terrain.NoiseHeightMap;
import org.terasology.math.TeraMath;

import com.google.common.collect.Sets;

/**
 * Uses world generation code to draw on a swing canvas
 * @author Martin Steiger
 */
public class SwingRasterizer {

    private final WorldFacade facade;
    private NoiseHeightMap heightMap;
    
    /**
     * @param seed the seed value
     */
    public SwingRasterizer(String seed) {
        heightMap = new NoiseHeightMap();
        heightMap.setSeed(seed);
        
        facade = new WorldFacade(seed, heightMap);
    }

    /**
     * @param g the graphics object
     * @param sector the sector to render
     */
    public void rasterizeSector(Graphics2D g, Sector sector) {

        drawNoiseBackground(g, sector);

        drawRoads(g, sector);

        drawCities(g, sector);
        
        drawFrame(g, sector);
        drawSectorText(g, sector);
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
        for (Orientation dir : Sector.Orientation.values()) {
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
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Color.MAGENTA);
        int width = fm.stringWidth(text);
        int cx = (int) ((ci.getPos().x) * Sector.SIZE);
        int cz = (int) ((ci.getPos().y) * Sector.SIZE);
        g.drawString(text, cx - width / 2, cz + fm.getAscent() + 5);
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
    }
}
