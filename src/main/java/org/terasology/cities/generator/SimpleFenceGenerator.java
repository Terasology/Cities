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

import java.awt.Rectangle;
import java.util.Objects;

import org.terasology.cities.common.Orientation;
import org.terasology.cities.model.City;
import org.terasology.cities.model.SimpleFence;
import org.terasology.math.Vector2i;
import org.terasology.utilities.random.MersenneRandom;
import org.terasology.utilities.random.Random;

/**
 * Creates a {@link SimpleFence} for a rectangular lot shape
 * @author Martin Steiger
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
