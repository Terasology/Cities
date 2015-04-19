/*
 * Copyright 2015 MovingBlocks
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

package org.terasology.cities.swing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;
import org.terasology.math.Vector2i;

import org.terasology.cities.generator.RoadGeneratorSimple;
import org.terasology.cities.model.Junction;
import org.terasology.cities.model.Road;
import org.terasology.cities.model.Site;
import org.terasology.cities.noise.Wave;
import org.terasology.commonworld.UnorderedPair;
import org.terasology.commonworld.geom.Point2cd;
import org.terasology.commonworld.geom.Point2d;
import org.terasology.commonworld.geom.Point2md;

import com.google.common.base.Function;

/**
 * Visual test of {@link Wave}
 */
public final class WavedLineTest {

    private WavedLineTest() {
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

                Function<Vector2i, Junction> junctions = new Function<Vector2i, Junction>() {

                    @Override
                    public Junction apply(Vector2i input) {
                        return new Junction(input);
                    }

                };

                Point2d start = new Point2cd(50, 100);
                Point2d end = new Point2cd(450, 200);
                Site a = new Site(50, 100, 1);
                Site b = new Site(450, 200, 1);
                Road road = new RoadGeneratorSimple(junctions).apply(new UnorderedPair<Site>(a, b));

                Random r = new Random();
                Wave w0 = Wave.getSine(1.0, new double[] {r.nextDouble() - 0.5});
                Wave w1 = Wave.getSine(0.5, new double[] {r.nextDouble() - 0.5, r.nextDouble() - 0.5});
                Wave w2 = Wave.getSine(0.25, new double[] {r.nextDouble() - 0.5, r.nextDouble() - 0.5, r.nextDouble() - 0.5, r.nextDouble() - 0.5});

                int oldX = 0;
                int oldY = 0;

                int cnt = road.getPoints().size();

                for (int i = 0; i < cnt; i++) {
                    double ip = (i + 1.0) / (cnt + 1);
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

                g.drawOval(a.getPos().x - 4, a.getPos().y - 4, 8, 8);
                g.drawOval(b.getPos().x - 4, b.getPos().y - 4, 8, 8);

            }

        });

        frame.setLocation(500, 200);
        frame.setSize(600, 400);
        frame.setVisible(true);
    }
}
