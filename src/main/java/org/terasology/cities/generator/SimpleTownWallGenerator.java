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

import org.terasology.cities.model.City;
import org.terasology.cities.model.SimpleTower;
import org.terasology.cities.model.SolidWallSegment;
import org.terasology.cities.model.TownWall;
import org.terasology.cities.model.WallSegment;
import org.terasology.math.Vector2i;
import org.terasology.utilities.random.MersenneRandom;
import org.terasology.utilities.random.Random;

import com.google.common.base.Function;

/**
 * Generates a {@link TownWall} around a given settlement.
 * Does not respect blocked areas or roads
 * @author Martin Steiger
 */
public class SimpleTownWallGenerator {

    private String seed;
    private Function<Vector2i, Integer> heightMap;

    /**
     * @param seed the random seed
     * @param heightMap the terrain height function
     */
    public SimpleTownWallGenerator(String seed, Function<Vector2i, Integer> heightMap) {
        this.seed = seed;
        this.heightMap = heightMap;
    }

    /**
     * @param city the city
     * @return a town wall
     */
    public TownWall generate(City city) {
        Random rand = new MersenneRandom(Objects.hash(seed, city.hashCode()));

        int cx = city.getPos().x;
        int cz = city.getPos().y;
       
        int minSegments = 5 + (int) (city.getDiameter() / 30);    // must be > 0
        int maxSegments = 5 + (int) (city.getDiameter() / 20);
        double maxAngularDiv = Math.toRadians(15);
        double maxRadiusDiv = city.getDiameter() * 0.02;
        int maxWallThick = 4;
        int wallHeight = 9;
        int towerHeight = 10;
        int wallThick = maxWallThick;
        
        double maxRad = (city.getDiameter() - maxWallThick) * 0.5;
        
        TownWall tw = new TownWall();
        
        int segments = rand.nextInt(minSegments, maxSegments);
        
        double ang = rand.nextDouble(0, Math.PI);   // actually, the range doesn't matter

        // generate all wall segments
        Vector2i prevPos = null;
        for (int i = 0; i < segments; i++) {
            double rad = maxRad - rand.nextDouble() * maxRadiusDiv;
            
            // don't add the random to ang itself, because the error might add up
            double pAng = ang + (rand.nextDouble() - 0.5) * maxAngularDiv;
            int tx = cx + (int) (rad * Math.cos(pAng));
            int ty = cz + (int) (rad * Math.sin(pAng));
            Vector2i pos = new Vector2i(tx, ty);
            
            if (prevPos != null) {
                tw.addWall(new SolidWallSegment(prevPos, pos, wallThick, wallHeight));
            }
            
            prevPos = pos;
            ang += (Math.PI * 2 / (segments));
        }
        
        // close the circle
        Vector2i firstPoint = tw.getWalls().get(0).getStart();
        tw.addWall(new SolidWallSegment(prevPos, firstPoint, wallThick, wallHeight));
        
        // generate wall towers
        for (WallSegment wall : tw.getWalls()) {
            Vector2i center = wall.getStart();
            int towerRad = 3;
            // make sure the width/height are odd to make the BattlementRoof look pretty
            Rectangle layout = new Rectangle(center.x - towerRad, center.y - towerRad, towerRad * 2 - 1, towerRad * 2 - 1);
            int baseHeight = heightMap.apply(center);
            SimpleTower tower = new SimpleTower(layout, baseHeight, towerHeight);
            tw.addTower(tower);
        }
        
        return tw;
    }
}
