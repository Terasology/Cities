/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.cities.symmetry;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.vecmath.Point2i;

/**
 * A interactive test for different {@link Symmetry} implementations.
 * Use the main method to start.
 * @author Martin Steiger
 */
public final class InteractiveSymmetryTest {

    private InteractiveSymmetryTest() {
        // private
    }

    /**
     * @param args (ignored)
     */
    public static void main(String[] args) {
        // Create and set up the window.
        final JFrame frame = new JFrame("Symmetry Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final Symmetry sym = Symmetries.alongNegativeDiagonal();
        final Point pt = new Point();

        JComponent comp = new JComponent() {
            private static final long serialVersionUID = -3019274194814342555L;

            @Override
            protected void paintComponent(final Graphics g1) {
                super.paintComponent(g1);
                final Graphics2D g = (Graphics2D) g1;
                int scale = 16;
                int centerX = getWidth() / (2 * scale);
                int centerY = getHeight() / (2 * scale);
                g.scale(scale, scale);

                Point2i mp = new Point2i(pt.x / scale, pt.y / scale);

                double mouseX = mp.getX() + 0.5;
                double mouseY = mp.getY() + 0.5;

                g.setColor(Color.CYAN);
                g.draw(new Line2D.Double(mouseX, mouseY, mouseX, mouseY));

                mp.x -= centerX;
                mp.y -= centerY;

                mp = sym.getMirrored(mp);

                mouseX = centerX + mp.getX() + 0.5;
                mouseY = centerY + mp.getY() + 0.5;

                g.setColor(Color.BLUE);
                g.draw(new Line2D.Double(mouseX, mouseY, mouseX, mouseY));

                // draw grid
                g.setStroke(new BasicStroke(0f));
                g.setColor(Color.LIGHT_GRAY);
                for (int i = 0; i < getWidth() / scale + 1; i++) {
                    g.drawLine(i, 0, i, getHeight());
                }
                for (int i = 0; i < getHeight() / scale + 1; i++) {
                    g.drawLine(0, i, getWidth(), i);
                }

                g.setColor(Color.BLACK);
                g.drawLine(centerX, 0, centerX, getHeight());
                g.drawLine(0, centerY, getWidth(), centerY);

                g.setStroke(new BasicStroke());
            }
        };

        frame.add(comp);

        comp.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                mouseMoved(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                pt.x = e.getX();
                pt.y = e.getY();

                e.getComponent().repaint();
            }

        });

        frame.setLocation(500, 200);
        frame.setSize(600, 400);
        frame.setVisible(true);
    }
}
