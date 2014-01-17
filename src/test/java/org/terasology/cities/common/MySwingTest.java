/*
 * Copyright 2014 MovingBlocks
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

package org.terasology.cities.common;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.terasology.cities.raster.BresenhamCircle;
import org.terasology.cities.raster.BresenhamLine;
import org.terasology.cities.raster.BresenhamLine.ThicknessMode;
import org.terasology.cities.raster.PixelDrawer;
import org.terasology.math.TeraMath;

/**
 * Not really a JUnit test class
 * @author Martin Steiger
 */
public final class MySwingTest {
    
    private MySwingTest() {
        // empty
    }
    
    /**
     * @param args ignored
     */
    public static void main(String[] args) {
        // Create and set up the window.
        final JFrame frame = new JFrame("Murphy");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(new JComponent() {
            
            private static final long serialVersionUID = -3019274194814342555L;
            
            private double rot;

            @Override
            protected void paintComponent(final Graphics go) {
                super.paintComponent(go);
                final Graphics2D g = (Graphics2D) go;
                int scale = 8;
                g.scale(scale, scale);

                // draw grid
                g.setColor(Color.LIGHT_GRAY);
                g.setStroke(new BasicStroke(0f));
                for (int i = 0; i < getWidth() / scale; i++) {
                    g.drawLine(i, 0, i, getHeight());
                }
                for (int i = 0; i < getHeight() / scale; i++) {
                    g.drawLine(0, i, getWidth(), i);
                }

                g.setStroke(new BasicStroke());

                PixelDrawer pixelDrawer = new PixelDrawer() {
                    @Override
                    public void drawPixel(int x, int y, Color aColor) {
                        g.setColor(aColor);
                        g.draw(new Line2D.Double(x + 0.5, y + 0.5, x + 0.5, y + 0.5)); 
                    }
                };

                BresenhamLine murphy = new BresenhamLine(pixelDrawer);

                ThicknessMode mode = BresenhamLine.ThicknessMode.MIDDLE;
                Color black = new Color(0x000000);
                Color gray = new Color(0x404040);

                murphy.drawThickLine(22, 12, 83, 44, 5, mode, new Color(0x808000));
                murphy.drawThickLineSimple(12, 52, 74, 28, 5, mode, new Color(0xA0));
                murphy.drawThickLine(50, 90, 80, 90, 5, BresenhamLine.ThicknessMode.CLOCKWISE, gray);
                murphy.drawThickLine(30, 90, 100, 90, 1, BresenhamLine.ThicknessMode.MIDDLE, black);
                murphy.drawThickLine(50, 100, 80, 100, 5, BresenhamLine.ThicknessMode.COUNTERCLOCKWISE, gray);
                murphy.drawThickLine(30, 100, 100, 100, 1, BresenhamLine.ThicknessMode.MIDDLE, black);
                murphy.drawThickLine(50, 110, 80, 110, 5, BresenhamLine.ThicknessMode.MIDDLE, gray);
                murphy.drawThickLine(30, 110, 100, 110, 1, BresenhamLine.ThicknessMode.MIDDLE, black);

                murphy.drawThickLine(-15, 70, 10, 10, 8, BresenhamLine.ThicknessMode.MIDDLE, black);

                BresenhamCircle bc = new BresenhamCircle(pixelDrawer);
                
                for (int r = 1; r < 8; r++) {
                    bc.fillCircle(130, -10 + 15 * r, r, Color.GRAY);
                    bc.drawCircle(110, -10 + 15 * r, r, Color.BLUE);
                }

                murphy.drawThickLine(-15, 70, 10, 10, 8, BresenhamLine.ThicknessMode.MIDDLE, black);
                
                int drx = TeraMath.floorToInt(Math.sin(rot) * 15);
                int dry = TeraMath.floorToInt(Math.cos(rot) * 15);
                murphy.drawThickLine(50 + drx, 70 + dry, 50 - drx, 70 - dry, 3, mode, Color.MAGENTA);
                rot += Math.toRadians(5);
            }
        });

        Timer t = new Timer();
        t.schedule(new TimerTask() {

            @Override
            public void run() {
                frame.repaint();

            }
        }, 250, 250);

        frame.setLocation(500, 200);
        frame.setSize(600, 400);
        frame.setVisible(true);
    }
}
