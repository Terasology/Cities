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

import org.terasology.cities.common.Orientation;
import org.terasology.cities.model.HipRoof;
import org.terasology.cities.model.SaddleRoof;
import org.terasology.cities.model.SimpleBuildingPart;
import org.terasology.cities.model.SimpleChurch;
import org.terasology.cities.model.SimpleDoor;
import org.terasology.cities.model.SimpleLot;
import org.terasology.cities.terrain.HeightMap;
import org.terasology.math.Vector2i;
import org.terasology.utilities.random.MersenneRandom;
import org.terasology.utilities.random.Random;

/**
 * Creates {@link SimpleChurch}es
 * @author Martin Steiger
 */
public class SimpleChurchGenerator extends AbstractGenerator {

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
        Rectangle lotRc = new Rectangle(lot.getShape());
        lotRc.x += 1;
        lotRc.y += 1;
        lotRc.width -= 2;
        lotRc.height -= 2;
        
        boolean alignEast = (lotRc.width > lotRc.height);
        
        // build along larger axis and rotate later, if necessary
        int width = Math.max(lotRc.width, lotRc.height);
        int height = Math.min(lotRc.width, lotRc.height);
        
        int doorWidth = 2;
        int doorHeight = 4;
        double relationLength = 0.33;       // tower size compared to nave size
        double relationWidth = 2;

        int towerSize = (int) (width * relationLength);
        
        // make it odd, so that the tented roof looks nice (1 block thick at the center)
        if (towerSize % 2 == 0) {
            towerSize++;
        }
        
        int naveLen = width - towerSize;
        int naveWidth = (int) Math.min(height, towerSize * relationWidth);

        // make it odd, so it looks symmetric with the tower - make it smaller though
        if (naveLen % 2 == 0) {
            naveLen--;
        }
        
        int ty = (height - towerSize) / 2;
        int dy = (height - doorWidth) / 2;
        int ny = (height - naveWidth) / 2;
        Rectangle naveRect = new Rectangle(0, ny, naveLen, naveWidth);
        Rectangle towerRect = new Rectangle(naveLen, ty, towerSize, towerSize);
        Rectangle doorRc = new Rectangle(0, dy, 1, doorWidth);
        Orientation doorOrientation = Orientation.WEST;        
        
        int rot = alignEast ? 0 : 90;
        
        if (rand.nextBoolean()) {
            rot += 180;
        }
        
        naveRect = transformRect(naveRect, lotRc, rot);
        towerRect = transformRect(towerRect, lotRc, rot);
        doorRc = transformRect(doorRc, lotRc, rot);
        doorOrientation = doorOrientation.getRotated(rot);

        Vector2i doorDir = doorOrientation.getDir();
        Rectangle probeRc = new Rectangle(doorRc.x + doorDir.x, doorRc.y + doorDir.y, doorRc.width, doorRc.height);
        
        int baseHeight = getMaxHeight(probeRc) + 1; // 0 == terrain
        int towerHeight = baseHeight + 14;
        int hallHeight = baseHeight + 8;
        
        SimpleDoor door = new SimpleDoor(doorOrientation, doorRc, baseHeight, baseHeight + doorHeight);
        
        Rectangle naveRoofRect = new Rectangle(naveRect);
        naveRoofRect.x -= 1;
        naveRoofRect.y -= 1;
        naveRoofRect.width += 2;
        naveRoofRect.height += 2;

        Rectangle towerRoofRect = new Rectangle(towerRect);
        towerRoofRect.x -= 1;
        towerRoofRect.y -= 1;
        towerRoofRect.width += 2;
        towerRoofRect.height += 2;

        SaddleRoof naveRoof = new SaddleRoof(naveRoofRect, hallHeight, door.getOrientation(), 1);
        HipRoof towerRoof = new HipRoof(towerRoofRect, towerHeight, 2);

        SimpleBuildingPart nave = new SimpleBuildingPart(naveRect, baseHeight, hallHeight, naveRoof);
        SimpleBuildingPart tower = new SimpleBuildingPart(towerRect, baseHeight, towerHeight, towerRoof);
        SimpleChurch church = new SimpleChurch(nave, tower, door);
        
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
