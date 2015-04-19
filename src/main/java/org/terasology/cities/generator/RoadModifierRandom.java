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

package org.terasology.cities.generator;

import java.util.Objects;

import org.terasology.math.Vector2i;

import org.terasology.cities.model.Road;
import org.terasology.cities.noise.Wave;
import org.terasology.commonworld.geom.Point2cd;
import org.terasology.commonworld.geom.Point2d;
import org.terasology.commonworld.geom.Point2md;
import org.terasology.utilities.random.FastRandom;
import org.terasology.utilities.random.Random;

/**
 * Applies an overlay of different random wavelets to all road segments
 */
public class RoadModifierRandom  {

    private final double randomness;

    /**
     * Uses a randomness of 25
     */
    public RoadModifierRandom() {
        randomness = 0.5;
    }

   /**
     * @param randomness the amount of randomness (with respect to the length of the road) - default = 0.5
     */
    public RoadModifierRandom(double randomness) {
        this.randomness = randomness;
    }


    /**
     * Applies a Gaussian random number to all segments
     * @param road the road to modify
     */
    public void apply(Road road) {
        Vector2i startPos = road.getStart().getCoords();
        Vector2i endPos = road.getEnd().getCoords();
        Random r = new FastRandom(Objects.hash(startPos, endPos));

        Point2d start = new Point2cd(startPos.x, startPos.y);
        Point2d end = new Point2cd(endPos.x, endPos.y);
        double length = start.dist(end);
        double factor = length * randomness;

        Wave w0 = Wave.getHat(1.0, new double[] {r.nextDouble() - 0.5});
        Wave w1 = Wave.getHat(0.5, new double[] {r.nextDouble() - 0.5, r.nextDouble() - 0.5});
        Wave w2 = Wave.getHat(0.25, new double[] {r.nextDouble() - 0.5, r.nextDouble() - 0.5, r.nextDouble() - 0.5, r.nextDouble() - 0.5});

        int cnt = road.getPoints().size();
        for (int i = 0; i < cnt; i++) {
            double ip = (i + 1.0) / (cnt + 1);
            Point2md seg = Point2d.ipol(start, end, ip);

            seg.subY(w0.get(ip) * factor);
            seg.subY(w1.get(ip) * factor * 0.5);
            seg.subY(w2.get(ip) * factor * 0.25);

            int x = (int) (seg.getX() + 0.5);
            int y = (int) (seg.getY() + 0.5);

            Vector2i pt = road.getPoints().get(i); 
            pt.x = x;
            pt.y = y;
        }
    }
}
