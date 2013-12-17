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

package org.terasology.cities.testing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cities.model.Sector;
import org.terasology.cities.model.Sectors;
import org.terasology.cities.testing.swing.SwingRasterizer;
import org.terasology.math.Vector2i;

/**
 * A JComponent that displays the rasterized images using a virtual camera
 * @author Martin Steiger
 */
final class JCityComponent extends JComponent {
    private static final long serialVersionUID = 6918469720616969973L;

    private static final Logger logger = LoggerFactory.getLogger(JCityComponent.class);
    
    final BufferedImage image = new BufferedImage(3 * 256, 3 * 256, BufferedImage.TYPE_INT_ARGB);

    final Vector2i cameraPos = new Vector2i(50, 50);

    private JLabel label;

    private SwingRasterizer rasterizer;
    
    public JCityComponent(String seed, final JLabel label) {
        super();
        
        this.label = label;

        rasterizer = new SwingRasterizer(seed);

        addKeyListener(new KeyAdapter() {
            
            @Override
            public void keyPressed(KeyEvent e) {
                int moveInterval = 50;
                
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    cameraPos.x += moveInterval;
                    updateLabel();
                    repaint();
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    cameraPos.x -= moveInterval;
                    updateLabel();
                    repaint();
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    cameraPos.y += moveInterval;
                    updateLabel();
                    repaint();
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    cameraPos.y -= moveInterval;
                    updateLabel();
                    repaint();
                }
            }
        });
        
        updateLabel();
    }

    protected void updateLabel() {
        label.setText("LEFT, RIGHT, UP, DOWN - Camera: " + cameraPos.toString());
    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    @Override
    public Dimension getPreferredSize() {
        if (isPreferredSizeSet()) {
            return super.getPreferredSize();
        }

        return new Dimension(image.getWidth(), image.getHeight());
    }
    
    @Override
    protected void paintComponent(Graphics g1) {
        
        super.paintComponent(g1);

        final int imgWidth = image.getWidth();
        final int imgHeight = image.getHeight();

        Graphics2D g = image.createGraphics();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, imgWidth, imgHeight);

        int scale = 2;
             
        try {
            g.setColor(Color.BLACK);

            g.scale(scale, scale);
            g.translate(cameraPos.x, cameraPos.y);

            
            int camOffX = (int) Math.floor(cameraPos.x / (double) Sector.SIZE);
            int camOffZ = (int) Math.floor(cameraPos.y / (double) Sector.SIZE);

            int numX = imgWidth / (Sector.SIZE * scale) + 1;
            int numZ = imgHeight / (Sector.SIZE * scale) + 1;
            
            logger.debug("Drawing {}x{} tiles", numX + 1, numZ + 1);

            for (int z = -1; z < numZ; z++) {
                for (int x = -1; x < numX; x++) {
                    Vector2i coord = new Vector2i(x - camOffX, z - camOffZ);
                    Sector sector = Sectors.getSector(coord);
                    g.setClip((x - camOffX) * Sector.SIZE, (z - camOffZ) * Sector.SIZE, Sector.SIZE, Sector.SIZE);
                    rasterizer.rasterizeSector(g, sector);
                }
            }
            g.setClip(null);
        } finally {
            g.dispose();
        }
        
        g1.drawImage(image, 0, 0, null);
    }
    
}
