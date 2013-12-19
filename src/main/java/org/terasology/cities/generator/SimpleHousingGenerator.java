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
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cities.model.DomeRoof;
import org.terasology.cities.model.HipRoof;
import org.terasology.cities.model.Roof;
import org.terasology.cities.model.SaddleRoof;
import org.terasology.cities.model.SimpleBuilding;
import org.terasology.cities.model.SimpleHome;
import org.terasology.cities.model.SimpleLot;
import org.terasology.math.Vector2i;
import org.terasology.utilities.random.MersenneRandom;
import org.terasology.utilities.random.Random;

import com.google.common.base.Function;

/**
 * A very simple lot generator. It places square-shaped lots 
 * randomly in a circular area and checks whether it intersects or not.  
 * @author Martin Steiger
 */
public class SimpleHousingGenerator implements Function<SimpleLot, Set<SimpleBuilding>> {

    private static final Logger logger = LoggerFactory.getLogger(SimpleHousingGenerator.class);
    
    private final String seed;
    private Function<Vector2i, Integer> heightMap;
    
    /**
     * @param seed the random seed
     * @param heightMap the terrain height function
     */
    public SimpleHousingGenerator(String seed, Function<Vector2i, Integer> heightMap) {
        this.seed = seed;
        this.heightMap = heightMap;
    }

    /**
     * @param lot the lot
     * @return a single building
     */
    @Override
    public Set<SimpleBuilding> apply(SimpleLot lot) {
        // leave 1 block border for the building
        Rectangle lotRc = lot.getShape();
        
        // use the rectangle, not the lot itself, because its hashcode is the identity hashcode
        Random r = new MersenneRandom(Objects.hash(seed, lotRc));
        
        int inset = 2;
        Rectangle rc = new Rectangle(lotRc.x + inset, lotRc.y + inset, lotRc.width - 2 * inset, lotRc.height - 2 * inset);
        
        int wallHeight = 3;
        int doorWidth = 1;
        int doorHeight = 2;
        
        Rectangle door;
        if (r.nextBoolean()) {                               // on the x-axis
            int cx = rc.x + (rc.width - doorWidth) / 2; 
            if (r.nextBoolean()) {                           // on the top wall
                int y = rc.y;
                door = new Rectangle(cx, y, doorWidth, 1);
            } else {
                int y = rc.y + rc.height - 1;                // on the bottom wall
                door = new Rectangle(cx, y, doorWidth, 1);
            }
        } else {
            int cz = rc.y + (rc.height - doorWidth) / 2; 
            if (r.nextBoolean()) {                           // on the left wall
                int x = rc.x;
                door = new Rectangle(x, cz, 1, doorWidth);
            } else {                                         // on the right wall
                int x = rc.x + rc.width - 1;
                door = new Rectangle(x, cz, 1, doorWidth);
            }
        }
        
        // use door as base height - 
        // this is a bit dodgy, because only the first block is considered
        // maybe sample along width of the door and use the average?
        // also check the height _in front_ of the door
        // we add +1, because the building starts at 1 block above the terrain
        int baseHeight = heightMap.apply(new Vector2i(door.x, door.y)) + 1;
        
        // the roof area is 1 block larger all around
        Rectangle roofArea = new Rectangle(rc.x - 1, rc.y - 1, rc.width + 2, rc.height + 2);

        int roofBaseHeight = baseHeight + wallHeight;
        Roof roof = createRoof(r, roofArea, roofBaseHeight);

        SimpleBuilding simpleBuilding = new SimpleHome(rc, roof, baseHeight, wallHeight, door, doorHeight);
        
        logger.debug("Created 1 building for the lot");
        
        return Collections.singleton(simpleBuilding);
    }

    private Roof createRoof(Random r, Rectangle roofArea, int roofBaseHeight) {
        int type = r.nextInt(100);
        
        if (type < 33) {
            int roofPitch = 1;
            return new HipRoof(roofArea, roofBaseHeight, roofBaseHeight + 1, roofPitch);
        }
        
        if (type < 66) {
            return new DomeRoof(roofArea, roofBaseHeight, Math.min(roofArea.width, roofArea.height) / 2);
        }
        
        return new SaddleRoof(roofArea, roofBaseHeight, 1);
    }
}
