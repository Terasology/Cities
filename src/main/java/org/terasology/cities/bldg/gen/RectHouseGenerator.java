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

import java.math.RoundingMode;
import java.util.Set;

import org.terasology.cities.bldg.Building;
import org.terasology.cities.bldg.DefaultBuilding;
import org.terasology.cities.bldg.RectBuildingPart;
import org.terasology.cities.common.Edges;
import org.terasology.cities.door.SimpleDoor;
import org.terasology.cities.model.roof.DomeRoof;
import org.terasology.cities.model.roof.HipRoof;
import org.terasology.cities.model.roof.Roof;
import org.terasology.cities.model.roof.SaddleRoof;
import org.terasology.cities.parcels.Parcel;
import org.terasology.cities.surface.InfiniteSurfaceHeightFacet;
import org.terasology.cities.window.SimpleWindow;
import org.terasology.commonworld.Orientation;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.BaseVector2f;
import org.terasology.math.geom.ImmutableVector2i;
import org.terasology.math.geom.LineSegment;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.utilities.random.MersenneRandom;
import org.terasology.utilities.random.Random;

import com.google.common.collect.Sets;

/**
 *
 */
public class RectHouseGenerator {

    public Building apply(Parcel parcel, InfiniteSurfaceHeightFacet hm) {

        // use the rectangle, not the lot itself, because its hashcode is the identity hashcode
        Random rng = new MersenneRandom(parcel.getShape().hashCode());

        DefaultBuilding bldg = new DefaultBuilding(parcel.getOrientation());
        int inset = 2;
        Rect2i layout = parcel.getShape().expand(new Vector2i(-inset, -inset));

        LineSegment seg = Edges.getEdge(layout, parcel.getOrientation());
        Vector2i doorPos = new Vector2i(BaseVector2f.lerp(seg.getStart(), seg.getEnd(), 0.5f), RoundingMode.HALF_UP);

        // use door as base height -
        // this is a bit dodgy, because only the first block is considered
        // maybe sample along width of the door and use the average?
        ImmutableVector2i doorDir = parcel.getOrientation().getDir();
        Vector2i probePos = new Vector2i(doorPos.getX() + doorDir.getX(), doorPos.getY() + doorDir.getY());

        // we add +1, because the building starts at 1 block above the terrain
        int floorHeight = TeraMath.floorToInt(hm.getWorld(probePos)) + 1;
        int wallHeight = 3;

        int roofBaseHeight = floorHeight + wallHeight;

        // the roof area is 1 block larger all around
        Rect2i roofArea = layout.expand(new Vector2i(1, 1));
        Roof roof = createRoof(rng, roofArea, roofBaseHeight);

        RectBuildingPart part = new RectBuildingPart(layout, roof, floorHeight, wallHeight);
        bldg.addPart(part);

        int doorHeight = 2;

        SimpleDoor door = new SimpleDoor(parcel.getOrientation(), doorPos, floorHeight, floorHeight + doorHeight);
        part.addDoor(door);

        for (int i = 0; i < 3; i++) {
            // use the other three cardinal directions to place windows
            Orientation orient = door.getOrientation().getRotated(90 * (i + 1));
            Set<SimpleWindow> wnds = createWindows(layout, floorHeight, orient);

            for (SimpleWindow wnd : wnds) {
                // test if terrain outside window is lower than window base height
                ImmutableVector2i wndDir = wnd.getOrientation().getDir();
                ImmutableVector2i wndPos = wnd.getPos();
                Vector2i probePosWnd = new Vector2i(wndPos.getX() + wndDir.getX(), wndPos.getY() + wndDir.getY());
                if (wnd.getHeight() > hm.getWorld(probePosWnd)) {
                    part.addWindow(wnd);
                }
            }
        }

        return bldg;
    }

    private Set<SimpleWindow> createWindows(Rect2i rc, int baseHeight, Orientation o) {

        final int wndBase = baseHeight + 1;
        final int endDist = 2;
        final int interDist = 2;
        final int wndSize = 1;

        Set<SimpleWindow> result = Sets.newHashSet();

        LineSegment borderSeg = Edges.getEdge(rc, o);
        Rect2i border = Rect2i.createEncompassing(new Vector2i(borderSeg.getStart()), new Vector2i(borderSeg.getEnd()));
        int step = interDist + wndSize;

        int firstX = border.minX() + endDist;
        int lastX = border.minX() + border.width() - endDist * 2;

        for (int x = firstX; x <= lastX; x += step) {
            Vector2i pos = new Vector2i(x, border.minY());
            SimpleWindow w = new SimpleWindow(o, pos, wndBase);
            result.add(w);
        }

        int firstY = border.minY() + endDist;
        int lastY = border.minY() + border.height() - endDist * 2;

        for (int y = firstY; y <= lastY; y += step) {
            Vector2i pos = new Vector2i(border.minX(), y);
            SimpleWindow w = new SimpleWindow(o, pos, wndBase);
            result.add(w);
        }

        return result;
    }

    private Roof createRoof(Random r, Rect2i roofArea, int roofBaseHeight) {
        int type = r.nextInt(100);

        if (type < 33) {
            int roofPitch = 1;
            return new HipRoof(roofArea, roofBaseHeight, roofPitch, roofBaseHeight + 1);
        }

        if (type < 66) {
            return new DomeRoof(roofArea, roofBaseHeight, Math.min(roofArea.width(), roofArea.height()) / 2);
        }

        boolean alongX = (roofArea.width() > roofArea.height());
        Orientation o = alongX ? Orientation.EAST : Orientation.NORTH;

        return new SaddleRoof(roofArea, roofBaseHeight, o, 1);
    }

}
