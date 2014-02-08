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

import java.awt.Point;
import java.awt.Rectangle;

import org.terasology.cities.common.Orientation;
import org.terasology.cities.common.Rectangles;
import org.terasology.cities.heightmap.HeightMap;
import org.terasology.cities.model.SimpleLot;
import org.terasology.cities.model.bldg.SimpleBuildingPart;
import org.terasology.cities.model.bldg.SimpleChurch;
import org.terasology.cities.model.bldg.SimpleDoor;
import org.terasology.cities.model.bldg.SimpleWindow;
import org.terasology.cities.model.roof.HipRoof;
import org.terasology.cities.model.roof.PentRoof;
import org.terasology.cities.model.roof.SaddleRoof;
import org.terasology.math.Vector2i;
import org.terasology.utilities.random.MersenneRandom;
import org.terasology.utilities.random.Random;

/**
 * Creates {@link SimpleChurch}es
 * @author Martin Steiger
 */
public class SimpleChurchGenerator {

    private final String seed;
    private final HeightMap heightMap;

    /**
     * @param seed the seed 
     * @param heightMap the height map
     */
    public SimpleChurchGenerator(String seed, HeightMap heightMap) {
        this.seed = seed;
        this.heightMap = heightMap;
    }

    /**
     * @param lot the lot to use
     * @return a generated {@link SimpleChurch} model
     */
    public SimpleChurch generate(SimpleLot lot) {
        
        Random rand = new MersenneRandom(seed.hashCode());      // TODO: take sector into account
        
        // make build-able area 1 block smaller, so make the roof stay inside 
        Rectangle lotRc = Rectangles.expandRect(lot.getShape(), -1);
        
        boolean alignEast = (lotRc.width > lotRc.height);
        
        // build along larger axis and rotate later, if necessary
        int width = Math.max(lotRc.width, lotRc.height);
        int height = Math.min(lotRc.width, lotRc.height);
        
        int sideOff = 3;
        int sideWidth = 5;
        int entranceWidth = 3;
        int entranceHeight = 4;
        double relationLength = 0.2;       // tower size compared to nave size
        double relationWidth = 2.0;

        int towerSize = (int) (width * relationLength);
        
        // make it odd, so that the tented roof looks nice (1 block thick at the center)
        if (towerSize % 2 == 0) {
            towerSize++;
        }
        
        int naveLen = width - towerSize;
        int naveWidth = (int) Math.min(height - 2 * sideWidth, towerSize * relationWidth);

        // make it odd, so it looks symmetric with the tower - make it smaller though
        if (naveWidth % 2 == 0) {
            naveWidth--;
        }
        
        int ty = (height - towerSize) / 2;
        int dy = (height - entranceWidth) / 2;
        int ny = (height - naveWidth) / 2;
        Rectangle naveRect = new Rectangle(0, ny, naveLen, naveWidth);              
        Rectangle towerRect = new Rectangle(naveLen - 1, ty, towerSize, towerSize); // -1 makes them overlap
        Rectangle doorRc = new Rectangle(0, dy, 1, entranceWidth);
        Orientation doorOrientation = Orientation.WEST;        

        Rectangle aisleLeftRc = new Rectangle(sideOff, ny - sideWidth + 1, naveLen - 2 * sideOff, sideWidth);    // make them overlap
        Rectangle aisleRightRc = new Rectangle(sideOff, ny + naveWidth - 1, naveLen - 2 * sideOff, sideWidth);   // make them overlap
        
        int rot = alignEast ? 0 : 90;
        
        if (rand.nextBoolean()) {
            rot += 180;
        }
        
        Point center = new Point(width / 2, height / 2);
        naveRect = Rectangles.transformRect(naveRect, lotRc, center, rot);
        towerRect = Rectangles.transformRect(towerRect, lotRc, center, rot);
        doorRc = Rectangles.transformRect(doorRc, lotRc, center, rot);
        aisleLeftRc = Rectangles.transformRect(aisleLeftRc, lotRc, center, rot);
        aisleRightRc = Rectangles.transformRect(aisleRightRc, lotRc, center, rot);
        doorOrientation = doorOrientation.getRotated(rot);
        Orientation leftOrient = Orientation.SOUTH.getRotated(rot);
        Orientation rightOrient = Orientation.NORTH.getRotated(rot);
        
        Vector2i doorDir = doorOrientation.getDir();
        Rectangle probeRc = new Rectangle(doorRc.x + doorDir.x, doorRc.y + doorDir.y, doorRc.width, doorRc.height);
        
        int baseHeight = getMaxHeight(probeRc) + 1; // 0 == terrain
        int towerHeight = baseHeight + 22;
        int hallHeight = baseHeight + 9;
        int sideHeight = baseHeight + 4;
        
        SimpleDoor entrance = new SimpleDoor(doorOrientation, doorRc, baseHeight, baseHeight + entranceHeight);
        
        Rectangle naveRoofRect = Rectangles.expandRect(naveRect, 1);
        Rectangle towerRoofRect = Rectangles.expandRect(towerRect, 1);

        SaddleRoof naveRoof = new SaddleRoof(naveRoofRect, hallHeight, entrance.getOrientation(), 1);
        HipRoof towerRoof = new HipRoof(towerRoofRect, towerHeight, 2);
        PentRoof roofLeft = new PentRoof(Rectangles.expandRect(aisleLeftRc, 1), sideHeight, leftOrient, 0.5);
        PentRoof roofRight = new PentRoof(Rectangles.expandRect(aisleRightRc, 1), sideHeight, rightOrient, 0.5);

        SimpleBuildingPart nave = new SimpleBuildingPart(naveRect, baseHeight, hallHeight, naveRoof);
        SimpleBuildingPart tower = new SimpleBuildingPart(towerRect, baseHeight, towerHeight, towerRoof);
        SimpleChurch church = new SimpleChurch(nave, tower, entrance);

        SimpleBuildingPart aisleLeft = new SimpleBuildingPart(aisleLeftRc, baseHeight, sideHeight, roofLeft);
        SimpleBuildingPart aisleRight = new SimpleBuildingPart(aisleRightRc, baseHeight, sideHeight, roofRight);

        church.addPart(aisleLeft);
        church.addPart(aisleRight);
        
        // create and add tower windows
        for (int i = 0; i < 3; i++) {
            // use the other three cardinal directions to place windows
            Orientation orient = entrance.getOrientation().getRotated(90 * (i + 1));
            Rectangle towerBorder = Rectangles.getBorder(towerRect, orient);

            int tBX = towerBorder.x + towerBorder.width / 2;
            int tBZ = towerBorder.y + towerBorder.height / 2;
            Rectangle wndRect = new Rectangle(tBX, tBZ, 1, 1);
            church.addWindow(new SimpleWindow(orient, wndRect, towerHeight - 3, towerHeight - 1));
        }
        
        return church;
    }

    private int getMaxHeight(Rectangle rc) {
        int maxHeight = Integer.MIN_VALUE;
        
        for (int z = rc.y; z < rc.y + rc.height; z++) {
            for (int x = rc.x; x < rc.x + rc.width; x++) {
                int height = heightMap.apply(x, z);
                if (maxHeight < height) {
                    maxHeight = height;
                }
            }
        }
        
        return maxHeight;
    }
}
