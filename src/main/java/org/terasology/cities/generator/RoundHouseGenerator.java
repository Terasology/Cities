/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.cities.generator;

import java.awt.Rectangle;
import java.math.RoundingMode;

import org.terasology.cities.model.SimpleLot;
import org.terasology.cities.model.bldg.RoundHouse;
import org.terasology.cities.model.bldg.SimpleDoor;
import org.terasology.cities.model.bldg.SimpleWindow;
import org.terasology.commonworld.Orientation;
import org.terasology.commonworld.geom.Rectangles;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.math.geom.ImmutableVector2i;

import com.google.common.math.IntMath;

/**
 * Generates round houses
 */
public class RoundHouseGenerator {
    private HeightMap heightMap;

    /**
     * @param heightMap the height map
     */
    public RoundHouseGenerator(HeightMap heightMap) {
        this.heightMap = heightMap;
    }

    /**
     * @param lot the lot to use
     * @return a generated {@link RoundHouse} model
     */
    public RoundHouse generate(SimpleLot lot) {

        // make build-able area 1 block smaller, so make the roof stay inside
        Rectangle lotRc = Rectangles.expandRect(lot.getShape(), -1);

        int centerX = lotRc.x + IntMath.divide(lotRc.width, 2, RoundingMode.HALF_UP);
        int centerY = lotRc.y + IntMath.divide(lotRc.height, 2, RoundingMode.HALF_UP);

        int towerSize = Math.min(lotRc.width, lotRc.height);
        int towerRad = towerSize / 2;

        int entranceWidth = 1;
        int entranceHeight = 2;
        Rectangle doorRc = new Rectangle(centerX + towerRad, centerY, 1, entranceWidth);
        Orientation doorOrientation = Orientation.EAST;

        ImmutableVector2i doorDir = doorOrientation.getDir();
        Rectangle probeRc = new Rectangle(doorRc.x + doorDir.getX(), doorRc.y + doorDir.getY(), doorRc.width, doorRc.height);

        int baseHeight = heightMap.apply(probeRc.x , probeRc.y);
        int sideHeight = 4;

        RoundHouse house = new RoundHouse(new ImmutableVector2i(centerX, centerY), towerRad, baseHeight, sideHeight);

        SimpleDoor entrance = new SimpleDoor(doorOrientation, doorRc, baseHeight, baseHeight + entranceHeight);
        house.setDoor(entrance);

        int windowWidth = 1;
        Rectangle wndRc1 = new Rectangle(centerX - towerRad, centerY, 1, windowWidth);
        Rectangle wndRc2 = new Rectangle(centerX, centerY - towerRad, 1, windowWidth);
        Rectangle wndRc3 = new Rectangle(centerX, centerY + towerRad, 1, windowWidth);
        SimpleWindow wnd1 = new SimpleWindow(Orientation.WEST, wndRc1, baseHeight + 1, baseHeight + 2);
        SimpleWindow wnd2 = new SimpleWindow(Orientation.NORTH, wndRc2, baseHeight + 1, baseHeight + 2);
        SimpleWindow wnd3 = new SimpleWindow(Orientation.SOUTH, wndRc3, baseHeight + 1, baseHeight + 2);

        house.addWindow(wnd1);
        house.addWindow(wnd2);
        house.addWindow(wnd3);

        return house;
    }
}
