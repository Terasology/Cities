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
