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

package org.terasology.cities.generator;

import java.util.Objects;

import javax.vecmath.Point2i;

import org.terasology.cities.model.Road;
import org.terasology.utilities.random.FastRandom;
import org.terasology.utilities.random.Random;

/**
 * Applies a Gaussian random number to all road segments
 * @author Martin Steiger
 */
public class RoadModifierRandom  {

    private double randomness;

    /**
     * Uses a randomness of 25
     */
    public RoadModifierRandom() {
        randomness = 25;
    }

   /**
     * @param randomness the amount of randomness (based on Gaussian normal distribution) - default = 0.02
     */
    public RoadModifierRandom(double randomness) {
        this.randomness = randomness;
    }


    /**
     * Applies a Gaussian random number to all segments
     * @param road the road to modify
     */
    public void apply(Road road) {
        Point2i startPos = road.getStart().getCoords();
        Point2i endPos = road.getEnd().getCoords();
        Random r = new FastRandom(Objects.hash(startPos, endPos));

        for (Point2i n : road.getPoints()) {

            n.x += r.nextGaussian() * randomness;
            n.y += r.nextGaussian() * randomness;

        }
    }
}
