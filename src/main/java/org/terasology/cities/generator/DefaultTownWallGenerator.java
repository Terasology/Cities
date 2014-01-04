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

import java.awt.Rectangle;
import java.util.List;
import java.util.Objects;

import org.terasology.cities.AreaInfo;
import org.terasology.cities.model.City;
import org.terasology.cities.model.GateWallSegment;
import org.terasology.cities.model.SimpleTower;
import org.terasology.cities.model.SolidWallSegment;
import org.terasology.cities.model.TownWall;
import org.terasology.cities.model.WallSegment;
import org.terasology.math.Vector2i;
import org.terasology.utilities.random.MersenneRandom;
import org.terasology.utilities.random.Random;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * Generates a {@link TownWall} around a given settlement
 * while respecting blocked areas
 * @author Martin Steiger
 */
public class DefaultTownWallGenerator {

    private String seed;
    private Function<Vector2i, Integer> heightMap;

    /**
     * @param seed the random seed
     * @param heightMap the terrain height function
     */
    public DefaultTownWallGenerator(String seed, Function<Vector2i, Integer> heightMap) {
        this.seed = seed;
        this.heightMap = heightMap;
    }

    /**
     * @param city the city
     * @param sectorInfo the blocked area
     * @return a town wall
     */
    public TownWall generate(City city, AreaInfo sectorInfo) {
        Random rand = new MersenneRandom(Objects.hash(seed, city.hashCode()));
        
        int cx = city.getPos().x;
        int cz = city.getPos().y;
        
        Vector2i center = new Vector2i(cx, cz);
        
        int maxWallThick = 4;
        double maxRad = (city.getDiameter() - maxWallThick) * 0.5;

        TownWall tw = new TownWall();
        
        double step = Math.toRadians(5);

        // generate towers first
        List<Vector2i> tp = getTowerPosList(rand, center, maxRad, step);
        List<Boolean> blocked = getBlockedPos(tp, sectorInfo);
        int lastHit = Short.MIN_VALUE;
        int firstHit = Short.MIN_VALUE;
        
        for (int i = 0; i < tp.size(); i++) {

            boolean thisOk = blocked.get(i);
            boolean nextOk = blocked.get((i + 1) % tp.size());
            boolean prevOk = blocked.get((i + tp.size() - 1) % tp.size());

            if (thisOk) { 

                if (!prevOk) {                          // the previous location was blocked -> this is the second part of a gate
                    tw.addTower(createGateTower(tp.get(i)));
                    
                    if (firstHit < 0) {
                        firstHit = i;
                    }

                    if (lastHit >= 0) {
                        Vector2i start = tp.get(lastHit);
                        Vector2i end = tp.get(i);
                        
                        tw.addWall(createGateWall(start, end));
                    }
                    lastHit = i;
                } else
                
                if (!nextOk) {                          // the next location is blocked -> this is the first part of a gate
                    tw.addTower(createGateTower(tp.get(i)));
                    
                    if (firstHit < 0) {
                        firstHit = i;
                    }
                    if (lastHit >= 0) {
                        Vector2i start = tp.get(lastHit);
                        Vector2i end = tp.get(i);

                        tw.addWall(createSolidWall(start, end));
                    }
                    lastHit = i;
                } else
                    
                if (i - lastHit > 5 && tp.size() + i - firstHit > 5) {          // the last/next tower is n segments away -> place another one
                    tw.addTower(createTower(tp.get(i)));
                    
                    if (firstHit < 0) {
                        firstHit = i;
                    }

                    if (lastHit >= 0) {
                        Vector2i start = tp.get(lastHit);
                        Vector2i end = tp.get(i);
                        tw.addWall(createSolidWall(start, end));
                    }
                    lastHit = i;
                }
            }
        }

        // connect first and last tower to close the circle 
        // --> this could be a gate segment 
        // --> TODO: find out
        if (firstHit >= 0 && lastHit >= 0) {
            Vector2i start = tp.get(lastHit);
            Vector2i end = tp.get(firstHit);
            tw.addWall(createSolidWall(start, end));
        }
        
        return tw;
    }
    
    private WallSegment createSolidWall(Vector2i start, Vector2i end) {
        int wallThick = 4;
        int wallHeight = 8;

        WallSegment wall = new SolidWallSegment(start, end, wallThick, wallHeight);
        return wall;
    }
    
    private WallSegment createGateWall(Vector2i start, Vector2i end) {
        int wallHeight = 8;
        int wallThick = 4;

        WallSegment wall = new GateWallSegment(start, end, wallThick, wallHeight);
        return wall;
    }

    private List<Boolean> getBlockedPos(List<Vector2i> tp, AreaInfo sectorInfo) {
        List<Boolean> list = Lists.newArrayList();
        
        for (Vector2i pos : tp) {
            Rectangle layout = getTowerRect(pos);
            boolean ok = !sectorInfo.isBlocked(layout);
            list.add(Boolean.valueOf(ok));
        }
        
        return list;
    }

    private SimpleTower createGateTower(Vector2i towerPos) {
        int towerHeight = 10;
        int baseHeight = heightMap.apply(towerPos);
        
        Rectangle layout = getTowerRect(towerPos);
        SimpleTower tower = new SimpleTower(layout, baseHeight, towerHeight);
        return tower;
    }

    private SimpleTower createTower(Vector2i towerPos) {
        int towerHeight = 9;
        int baseHeight = heightMap.apply(towerPos);
        
        Rectangle layout = getTowerRect(towerPos);
        SimpleTower tower = new SimpleTower(layout, baseHeight, towerHeight);
        return tower;
    }
    
    private List<Vector2i> getTowerPosList(Random rand, Vector2i center, double maxRad, double step) {

        double maxRadiusDiv = maxRad * 0.1;
  
        double maxAngularDiv = step * 0.1;

        List<Vector2i> list = Lists.newArrayList();
           
        double ang = rand.nextDouble(0, Math.PI);   // actually, the range doesn't matter
        double endAng = ang + Math.PI * 2.0 - step; // subtract one step to avoid duplicate first/last position

        while (ang < endAng) {
            double rad = maxRad - rand.nextDouble() * maxRadiusDiv;

            int tx = center.x + (int) (rad * Math.cos(ang));
            int ty = center.y + (int) (rad * Math.sin(ang));
            
            list.add(new Vector2i(tx, ty));

            double angDiv = (rand.nextDouble() - 0.5) * maxAngularDiv;
            ang += step + angDiv;
       }
        
        return list;
    }
    
    private Rectangle getTowerRect(Vector2i tp) {
        int towerRad = 3;

        // make sure the width/height are odd to make the BattlementRoof look pretty
        return new Rectangle(tp.x - towerRad, tp.y - towerRad, towerRad * 2 - 1, towerRad * 2 - 1);

    }
}
