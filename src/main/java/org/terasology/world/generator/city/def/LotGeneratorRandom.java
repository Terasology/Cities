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

package org.terasology.world.generator.city.def;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Objects;
import java.util.Set;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.terasology.math.Vector2i;
import org.terasology.utilities.random.FastRandom;
import org.terasology.utilities.random.Random;
import org.terasology.world.generator.city.model.City;
import org.terasology.world.generator.city.model.HipRoof;
import org.terasology.world.generator.city.model.Roof;
import org.terasology.world.generator.city.model.Sector;
import org.terasology.world.generator.city.model.SimpleBuilding;
import org.terasology.world.generator.city.model.SimpleLot;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

/**
 * A very simple lot generator. It places square-shaped lots 
 * randomly in a circular area and checks whether it intersects or not.  
 * @author Martin Steiger
 */
public class LotGeneratorRandom implements Function<City, Set<SimpleLot>> {

    private final String seed;
    private final Function<Sector, Shape> blockedAreaFunc;
    private Function<Vector2i, Integer> heightMap;
    
    /**
     * @param seed the random seed
     * @param blockedAreaFunc describes the blocked area for a sector
     * @param heightMap the terrain height function
     */
    public LotGeneratorRandom(String seed, Function<Sector, Shape> blockedAreaFunc, Function<Vector2i, Integer> heightMap) {
        this.seed = seed;
        this.blockedAreaFunc = blockedAreaFunc;
        this.heightMap = heightMap;
    }

    /**
     * @param city the city
     * @return a set of lots for that city within the city radius
     */
    @Override
    public Set<SimpleLot> apply(City city) {
        Random rand = new FastRandom(Objects.hash(seed, city));
        
        Sector sector = city.getSector();
        Shape blockedArea = blockedAreaFunc.apply(sector);
        Point2d center = city.getPos();
        
        Set<SimpleLot> lots = Sets.newLinkedHashSet();  // the order is important for deterministic generation
        double minSize = 9d;
        double maxSize = 12d;
        double maxRad = (city.getDiameter() - maxSize) * 0.5;
        
        for (int i = 0; i < 100; i++) {
            double ang = rand.nextDouble(0, Math.PI * 2.0);
            double rad = rand.nextDouble(5 + maxSize * 0.5, maxRad);
            double desSize = rand.nextDouble(minSize, maxSize);
            
            double x = center.x * Sector.SIZE + rad * Math.cos(ang);
            double z = center.y * Sector.SIZE + rad * Math.sin(ang);
            
            Point2d pos = new Point2d(x, z);
            Vector2d maxSpace = getMaxSpace(pos, lots);

            int sizeX = (int) Math.min(desSize, maxSpace.x);
            int sizeZ = (int) Math.min(desSize, maxSpace.y);
            
            // check if enough space is available
            if (sizeX < minSize || sizeZ < minSize) {
                continue;
            }

            Rectangle shape = new Rectangle((int) (pos.x - sizeX * 0.5), (int) (pos.y - sizeZ * 0.5), sizeX, sizeZ);

            // check if lot intersects with blocked area
            if (blockedArea.intersects(shape)) {
                continue;
            }

            // all tests passed -> create and add
            SimpleLot lot = new SimpleLot(shape);
            lot.addBuilding(createBuilding(rand, shape));
            lots.add(lot);
        }
        
        return lots;
    }

    /**
     * @param r the random
     * @param lot the lot shape
     * @return a building
     */
    private SimpleBuilding createBuilding(Random r, Rectangle lot) {
        // leave 1 block border for the building
        Rectangle rc = new Rectangle(lot.x + 1, lot.y + 1, lot.width - 2, lot.height - 2);
        
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
        int baseHeight = heightMap.apply(new Vector2i(door.x, door.y));
        
        // in this case, this is exactly the lot area
        Rectangle roofArea = new Rectangle(rc.x - 1, rc.y - 1, rc.width + 2, rc.height + 2);

		int roofPitch = 1;
        int roofBaseHeight = baseHeight + wallHeight;
		Roof roof = new HipRoof(roofArea, roofBaseHeight, roofBaseHeight + 1, roofPitch);
        
        return new SimpleBuilding(rc, roof, baseHeight, wallHeight, door, doorHeight);
    }

    private Vector2d getMaxSpace(Point2d pos, Set<SimpleLot> lots) {
        double maxX = Double.MAX_VALUE;
        double maxZ = Double.MAX_VALUE;
        
        //      xxxxxxxxxxxxxxxxxxx
        //      x                 x             (p)
        //      x        o------- x--------------|
        //      x                 x
        //      xxxxxxxxxxxxxxxxxxx       dx
        //                         <------------->
        
        for (SimpleLot lot : lots) {
            Rectangle2D bounds = lot.getShape();
            double dx = Math.abs(pos.x - bounds.getCenterX()) - bounds.getWidth() * 0.5;
            double dz = Math.abs(pos.y - bounds.getCenterY()) - bounds.getHeight() * 0.5;
            
            // the point is inside -> abort
            if (dx <= 0 && dz <= 0) {
                return new Vector2d(0, 0);
            }
            
            // the point is diagonally outside -> restrict one of the two only
            if (dx > 0 && dz > 0) {
                // make the larger of the two smaller --> larger shape area
                if (dx > dz) {
                    maxX = Math.min(maxX, dx);
                } else {
                    maxZ = Math.min(maxZ, dz);
                }
            }
            
            // the z-axis is overlapping -> restrict x
            if (dx > 0 && dz <= 0) {
                maxX = Math.min(maxX, dx);
            }
            
            // the x-axis is overlapping -> restrict z
            if (dx <= 0 && dz > 0) {
                maxZ = Math.min(maxZ, dz);
            }
        }
        
        return new Vector2d(2 * maxX, 2 * maxZ);
    }
}
