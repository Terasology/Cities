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

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Objects;
import java.util.Set;

import javax.vecmath.Point2d;

import org.terasology.utilities.random.FastRandom;
import org.terasology.utilities.random.Random;
import org.terasology.world.generator.city.model.City;
import org.terasology.world.generator.city.model.Lot;
import org.terasology.world.generator.city.model.Sector;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

/**
 * A very simple lot generator. It places square-shaped lots 
 * randomly in a circular area and checks whether it intersects or not.  
 * @author Martin Steiger
 */
public class LotGeneratorRandom implements Function<City, Set<Lot>> {

    private final String seed;
    private final Function<Sector, Shape> blockedAreaFunc;
    
    /**
     * @param seed the random seed
     * @param blockedAreaFunc describes the blocked area for a sector
     */
    public LotGeneratorRandom(String seed, Function<Sector, Shape> blockedAreaFunc) {
        this.seed = seed;
        this.blockedAreaFunc = blockedAreaFunc;
    }

    /**
     * @param city the city
     * @return a set of lots for that city within the city radius
     */
    @Override
    public Set<Lot> apply(City city) {
        Random r = new FastRandom(Objects.hash(seed, city));
        
        Sector sector = city.getSector();
        Shape blockedArea = blockedAreaFunc.apply(sector);
        Point2d center = city.getPos();
        
        Set<Lot> lots = Sets.newHashSet();
        double minSize = 4d;
        double maxSize = 8d;
        double maxRad = (city.getDiameter() - maxSize) * 0.5;
        
        for (int i = 0; i < 100; i++) {
            double ang = r.nextDouble(0, Math.PI * 2.0);
            double rad = r.nextDouble(5 + maxSize * 0.5, maxRad);
            double desSize = r.nextDouble(minSize, maxSize);
            
            double x = center.x * Sector.SIZE + rad * Math.cos(ang);
            double z = center.y * Sector.SIZE + rad * Math.sin(ang);
            
            Point2d pos = new Point2d(x, z);
            double size = Math.min(desSize, getMaxSpace(pos, lots) - 1);

            Rectangle2D shape = new Rectangle2D.Double(pos.x - size * 0.5, pos.y - size * 0.5, size, size);

            // check if enough space is available
            if (size < minSize) {
                continue;
            }

            // check if lot intersects with blocked area
            if (blockedArea.intersects(shape)) {
                continue;
            }

            // all tests passed -> create and add
            Lot lot = new Lot(pos, shape);
            lots.add(lot);
        }
        
        return lots;
    }

    private double getMaxSpace(Point2d pos, Set<Lot> lots) {
        double max = Double.MAX_VALUE;
        
        for (Lot lot : lots) {
            Rectangle2D bounds = lot.getShape().getBounds2D();
            double lotExt = Math.max(bounds.getWidth(), bounds.getHeight()) * 0.5;
            double dist = pos.distance(lot.getPos()) - lotExt;
            if (dist < max) {
                max = dist;
            }
        }
        
        return max;
    }
}
