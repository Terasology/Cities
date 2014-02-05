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

package org.terasology.cities.noise;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.terasology.cities.common.Point2cd;
import org.terasology.cities.common.Point2d;
import org.terasology.cities.common.Point2md;

/**
 * Visual test of {@link Wave}
 * @author Martin Steiger
 */
public final class ComposedLine {
    
    private ComposedLine() {
        // empty
    }
    
    /**
     * @param args (ignored)
     */
    public static void main(String[] args) {
        // Create and set up the window.
        final JFrame frame = new JFrame("Advanced road segmentation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(new JComponent() {
            private static final long serialVersionUID = -3019274194814342555L;

            @Override
            protected void paintComponent(final Graphics g1) {
                super.paintComponent(g1);
                final Graphics2D g = (Graphics2D) g1;

                Random r = new Random(123212);
                Wave w0 = Wave.getHat(1.0, new double[] { r.nextDouble() - 0.5 });
                Wave w1 = Wave.getHat(0.5, new double[] { r.nextDouble() - 0.5, r.nextDouble() - 0.5 });
                Wave w2 = Wave.getHat(0.25, new double[] { r.nextDouble() - 0.5, r.nextDouble() - 0.5, r.nextDouble() - 0.5, r.nextDouble() - 0.5 });

                int cnt = 23;
                int oldX = 0;
                int oldY = 0;
                Point2cd start = new Point2cd(50, 200);
                Point2cd end = new Point2cd(450, 200);
                for (int i = 0; i < cnt; i++) {
                    double ip = (double) i / (cnt - 1);
                    Point2md seg = Point2d.ipol(start, end, ip);

                    seg.subY(w0.get(ip) * 200);
                    seg.subY(w1.get(ip) * 200);
                    seg.subY(w2.get(ip) * 100);

                    int x = (int) (seg.getX() + 0.5);
                    int y = (int) (seg.getY() + 0.5);
                    g.drawOval(x - 2, y - 2, 4, 4);
                    if (i > 0) {
                        g.drawLine(oldX, oldY, x, y);
                    }
                    oldX = x;
                    oldY = y;
                }

            }

        });

        frame.setLocation(500, 200);
        frame.setSize(600, 400);
        frame.setVisible(true);
    }
}
