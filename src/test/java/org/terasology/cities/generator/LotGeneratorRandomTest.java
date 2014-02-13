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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.vecmath.Point2i;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cities.AreaInfo;
import org.terasology.cities.CityWorldConfig;
import org.terasology.cities.common.CachingFunction;
import org.terasology.cities.heightmap.HeightMap;
import org.terasology.cities.heightmap.HeightMaps;
import org.terasology.cities.model.City;
import org.terasology.cities.model.MedievalTown;
import org.terasology.cities.model.Sector;
import org.terasology.cities.model.Sectors;
import org.terasology.cities.model.SimpleLot;
import org.terasology.cities.model.Site;
import org.terasology.cities.model.bldg.Building;
import org.terasology.cities.model.bldg.SimpleBuilding;

import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
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
        
        final CityWorldConfig config = new CityWorldConfig();
        final HeightMap heightMap = HeightMaps.constant(10);
        final Function<Sector, AreaInfo> sectorInfos = CachingFunction.wrap(new Function<Sector, AreaInfo>() {

            @Override
            public AreaInfo apply(Sector input) {
                return new AreaInfo(config, heightMap);
            }
        }); 
        Function<Sector, Set<Site>> cpr = CachingFunction.wrap(new SiteFinderRandom(seed, sectorInfos, minPerSector, maxPerSector, minSize, maxSize));
        
        for (int x = 0; x < sectors; x++) {
            for (int z = 0; z < sectors; z++) {
                Sector sector = Sectors.getSector(x, z);
                cpr.apply(sector);   // fill the cache
            }
        }
        
        LotGeneratorRandom lg = new LotGeneratorRandom(seed);
        SimpleHousingGenerator shg = new SimpleHousingGenerator(seed, heightMap);
        
        Stopwatch pLotGen = Stopwatch.createStarted();
        
        int lotCount = 0;
        int bdgCount = 0;
        for (int x = 0; x < sectors; x++) {
            for (int z = 0; z < sectors; z++) {
                Set<Site> sites = cpr.apply(Sectors.getSector(x, z));
                
                for (Site site : sites) {
                    Point2i pos = site.getPos();
                    int rad = site.getRadius();
                    Ellipse2D cityBbox = new Ellipse2D.Double(pos.x - rad, pos.y - rad, rad * 2, rad * 2);

                    City city = new MedievalTown("name", site.getPos(), rad);
                    Set<SimpleLot> lots = lg.generate(city, sectorInfos.apply(Sectors.getSector(x, z)));
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

        logger.info("Created {} lots with {} buildings in {}ms.", lotCount, bdgCount, pLotGen.elapsed(TimeUnit.MILLISECONDS));
    }
    
}

