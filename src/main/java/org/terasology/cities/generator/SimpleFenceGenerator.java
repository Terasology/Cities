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

import java.awt.Rectangle;
import java.util.Objects;

import org.terasology.cities.model.City;
import org.terasology.cities.model.SimpleFence;
import org.terasology.commonworld.Orientation;
import org.terasology.math.Vector2i;
import org.terasology.utilities.random.MersenneRandom;
import org.terasology.utilities.random.Random;

/**
 * Creates a {@link SimpleFence} for a rectangular lot shape
 */
public class SimpleFenceGenerator {

    private final String seed;
    
    /**
     * @param seed the seed
     */
    public SimpleFenceGenerator(String seed) {
        this.seed = seed;
    }

    /**
     * @param city the city (only required for the RNG seeding)
     * @param shape the lot shape
     * @return the created fence
     */
    public SimpleFence createFence(City city, Rectangle shape) {
        Random rand = new MersenneRandom(Objects.hash(seed, city));
        
        Rectangle fenceRc = new Rectangle(shape);
        Vector2i gatePos = new Vector2i();
        Orientation gateOrient = null;
        switch (rand.nextInt(4)) {
        case 0:
            gateOrient = Orientation.NORTH;
            gatePos.x = rand.nextInt(fenceRc.x + 1, fenceRc.x + fenceRc.width - 2);
            gatePos.y = fenceRc.y;
            break;
            
        case 1:
            gateOrient = Orientation.WEST;
            gatePos.x = fenceRc.x;
            gatePos.y = rand.nextInt(fenceRc.y + 1, fenceRc.y + fenceRc.height - 2);
            break;
            
        case 2:
            gateOrient = Orientation.SOUTH;
            gatePos.x = rand.nextInt(fenceRc.x + 1, fenceRc.x + fenceRc.width - 2);
            gatePos.y = fenceRc.y + fenceRc.height - 1;
            break;
            
        case 3:
            gateOrient = Orientation.EAST;
            gatePos.x = fenceRc.x + fenceRc.width - 1;
            gatePos.y = rand.nextInt(fenceRc.y + 1, fenceRc.y + fenceRc.height - 2);
            break;
        }
        
        return new SimpleFence(fenceRc, gateOrient, gatePos); 
    }
}
