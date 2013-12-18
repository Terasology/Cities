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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Set;

import javax.vecmath.Point2d;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cities.common.CachingFunction;
import org.terasology.cities.common.Profiler;
import org.terasology.cities.generator.CityPlacerRandom;
import org.terasology.cities.generator.LotGeneratorRandom;
import org.terasology.cities.generator.SimpleHousingGenerator;
import org.terasology.cities.model.Building;
import org.terasology.cities.model.City;
import org.terasology.cities.model.Sector;
import org.terasology.cities.model.Sectors;
import org.terasology.cities.model.SimpleBuilding;
import org.terasology.cities.model.SimpleLot;
import org.terasology.math.Vector2i;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * Tests {@link LotGeneratorRandom}
 * @author Martin Steiger
 */
public class LotGeneratorRandomTest  {

    private static final Logger logger = LoggerFactory.getLogger(LotGeneratorRandomTest.class);
    
    /**
     * Performs through tests and logs computation time and results
     */
    @Test
    public void test() {
        String seed = "asd";
        int sectors = 10;       // sectors * sectors will be created
        
        int minPerSector = 1;
        int maxPerSector = 3;
        int minSize = 30;
        int maxSize = 200;
        Function<Sector, Set<City>> cpr = CachingFunction.wrap(new CityPlacerRandom(seed, minPerSector, maxPerSector, minSize, maxSize));
        
        for (int x = 0; x < sectors; x++) {
            for (int z = 0; z < sectors; z++) {
                Vector2i coord = new Vector2i(x, z);
                Sector sector = Sectors.getSector(coord);
                cpr.apply(sector);   // fill the cache
            }
        }
        
        Function<Sector, Shape> blockedAreaFunc = constantFunction((Shape) (new Area()));
        Function<Vector2i, Integer> heightMap = constantFunction(5);
        
        LotGeneratorRandom lg = new LotGeneratorRandom(seed, blockedAreaFunc);
        SimpleHousingGenerator shg = new SimpleHousingGenerator(seed, heightMap);
        
        Profiler.start("lot-generator");
        
        int lotCount = 0;
        int bdgCount = 0;
        for (int x = 0; x < sectors; x++) {
            for (int z = 0; z < sectors; z++) {
                Vector2i coord = new Vector2i(x, z);
                Set<City> cities = cpr.apply(Sectors.getSector(coord));
                
                for (City city : cities) {
                    Point2d pos = new Point2d(city.getPos());
                    pos.x *= Sector.SIZE;
                    pos.y *= Sector.SIZE;
                    double rad = city.getDiameter() * 0.5;
                    Ellipse2D cityBbox = new Ellipse2D.Double(pos.x - rad, pos.y - rad, rad * 2, rad * 2);

                    Set<SimpleLot> lots = lg.apply(city);
                    List<SimpleLot> list = Lists.newArrayList(lots);
                    
                    lotCount += lots.size();
                    
                    for (SimpleLot lot : lots) {
                        Rectangle2D lotBbox = lot.getShape().getBounds2D();
                        
                        assertTrue("lot not in city bounding circle", cityBbox.contains(lotBbox));
                        
                        for (SimpleBuilding blg : shg.apply(lot)) {
                            lot.addBuilding(blg);
                        }
                        
                        for (Building bld : lot.getBuildings()) {
                            Rectangle2D bldgBbox = bld.getLayout().getBounds2D();
                            assertTrue("building not inside lot", lotBbox.contains(bldgBbox));
                        }
                        
                        bdgCount += lot.getBuildings().size();
                    }
                    
                    // test all lots pairwise for overlap
                    for (int i = 0; i < list.size(); i++) {
                        Rectangle a = list.get(i).getShape();
                        for (int j = i + 1; j < list.size(); j++) {
                            Rectangle b = list.get(j).getShape();
                            assertFalse("lots overlap", a.intersects(b));
                        }
                    }
                }
            }
        }

        logger.info("Created {} lots with {} buildings in {}", lotCount, bdgCount, Profiler.getAsString("lot-generator"));
    }
    
    private static <P, R> Function<P, R> constantFunction(final R val) {
        return new Function<P, R>() {
            @Override
            public R apply(P p) {
                return val;
            }
        };
    }
}

