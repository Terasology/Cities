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

package org.terasology.cities.bldg.gen;

import java.awt.Rectangle;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Set;

import org.terasology.cities.bldg.Building;
import org.terasology.cities.bldg.DefaultBuilding;
import org.terasology.cities.bldg.RectBuildingPart;
import org.terasology.cities.bldg.SimpleDoor;
import org.terasology.cities.bldg.SimpleRoundHouse;
import org.terasology.cities.bldg.SimpleWindow;
import org.terasology.cities.common.Edges;
import org.terasology.cities.model.roof.HipRoof;
import org.terasology.cities.model.roof.Roof;
import org.terasology.cities.parcels.Parcel;
import org.terasology.cities.surface.InfiniteSurfaceHeightFacet;
import org.terasology.commonworld.Orientation;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.BaseVector2f;
import org.terasology.math.geom.ImmutableVector2i;
import org.terasology.math.geom.LineSegment;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.utilities.random.FastRandom;
import org.terasology.utilities.random.Random;

import com.google.common.math.IntMath;

/**
 *
 */
public class DefaultBuildingGenerator implements BuildingGenerator {

    private long seed;

    public DefaultBuildingGenerator(long seed)  {
        this.seed = seed;
    }

    @Override
    public Set<Building> generate(Parcel parcel, InfiniteSurfaceHeightFacet heightFacet) {
        Random rng = new FastRandom(parcel.getShape().hashCode() ^ seed);
        Building b;
        if (rng.nextFloat() < 0.2f) {
            b = generateRoundHouse(parcel, heightFacet);
        } else {
            b = generateRectHouse(parcel, heightFacet);
        }
        return Collections.singleton(b);
    }

    private Building generateRectHouse(Parcel parcel, InfiniteSurfaceHeightFacet heightFacet) {

        DefaultBuilding b = new DefaultBuilding(parcel.getOrientation());
        Rect2i layout = parcel.getShape().expand(new Vector2i(-2, -2));

        LineSegment seg = Edges.getEdge(layout, parcel.getOrientation());
        Vector2i doorPos = new Vector2i(BaseVector2f.lerp(seg.getStart(), seg.getEnd(), 0.5f), RoundingMode.HALF_UP);

        int floorHeight = TeraMath.floorToInt(heightFacet.getWorld(doorPos.x(), doorPos.y()));
        int wallHeight = 3;

        int roofPitch = 1;
        int roofBaseHeight = floorHeight + wallHeight;
        Rect2i roofArea = new Rect2i(layout);
        roofArea.expand(new Vector2i(1, 1));
        Roof roof = new HipRoof(roofArea, roofBaseHeight, roofPitch, roofBaseHeight + 1);

        RectBuildingPart part = new RectBuildingPart(layout, roof, floorHeight, wallHeight);
        b.addPart(part);
        return b;
    }

    private Building generateRoundHouse(Parcel parcel, InfiniteSurfaceHeightFacet heightFacet) {

        // make build-able area 1 block smaller, so make the roof stay inside
        Rect2i lotRc = parcel.getShape().expand(new Vector2i(-1, -1));

        int centerX = lotRc.minX() + IntMath.divide(lotRc.width(), 2, RoundingMode.HALF_UP);
        int centerY = lotRc.minY() + IntMath.divide(lotRc.height(), 2, RoundingMode.HALF_UP);

        int towerSize = Math.min(lotRc.width(), lotRc.height());
        int towerRad = towerSize / 2;

        int entranceWidth = 1;
        int entranceHeight = 2;
        Rect2i doorRc = Rect2i.createFromMinAndSize(centerX + towerRad, centerY, 1, entranceWidth);
        Orientation orient = Orientation.EAST;

        ImmutableVector2i doorDir = orient.getDir();
        Rect2i probeRc = Rect2i.createFromMinAndSize(doorRc.minX() + doorDir.getX(), doorRc.minY() + doorDir.getY(),
                doorRc.width(), doorRc.height());

        int baseHeight = TeraMath.floorToInt(heightFacet.getWorld(probeRc.minX(), probeRc.minY()));
        int sideHeight = 4;

        SimpleRoundHouse house = new SimpleRoundHouse(orient, new ImmutableVector2i(centerX, centerY), towerRad, baseHeight, sideHeight);

        SimpleDoor entrance = new SimpleDoor(orient, doorRc, baseHeight, baseHeight + entranceHeight);
        house.getRoom().addDoor(entrance);

        int windowWidth = 1;
        Rect2i wndRc1 = Rect2i.createFromMinAndSize(centerX - towerRad, centerY, 1, windowWidth);
        Rect2i wndRc2 = Rect2i.createFromMinAndSize(centerX, centerY - towerRad, 1, windowWidth);
        Rect2i wndRc3 = Rect2i.createFromMinAndSize(centerX, centerY + towerRad, 1, windowWidth);
        SimpleWindow wnd1 = new SimpleWindow(Orientation.WEST, wndRc1, baseHeight + 1, baseHeight + 2);
        SimpleWindow wnd2 = new SimpleWindow(Orientation.NORTH, wndRc2, baseHeight + 1, baseHeight + 2);
        SimpleWindow wnd3 = new SimpleWindow(Orientation.SOUTH, wndRc3, baseHeight + 1, baseHeight + 2);

        house.getRoom().addWindow(wnd1);
        house.getRoom().addWindow(wnd2);
        house.getRoom().addWindow(wnd3);

        return house;
    }

}
