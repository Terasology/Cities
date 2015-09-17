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

import java.util.Set;

import org.terasology.cities.bldg.SimpleDoor;
import org.terasology.cities.bldg.SimpleRectHouse;
import org.terasology.cities.bldg.SimpleWindow;
import org.terasology.cities.common.Edges;
import org.terasology.cities.model.roof.DomeRoof;
import org.terasology.cities.model.roof.HipRoof;
import org.terasology.cities.model.roof.Roof;
import org.terasology.cities.model.roof.SaddleRoof;
import org.terasology.cities.parcels.Parcel;
import org.terasology.cities.surface.InfiniteSurfaceHeightFacet;
import org.terasology.commonworld.Orientation;
import org.terasology.math.TeraMath;
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

    public SimpleRectHouse apply(Parcel lot, InfiniteSurfaceHeightFacet hm) {
        // leave 1 block border for the building
        Rect2i lotRc = lot.getShape();

        // use the rectangle, not the lot itself, because its hashcode is the identity hashcode
        Random r = new MersenneRandom(lotRc.hashCode());

        int inset = 2;
        Rect2i rc = Rect2i.createFromMinAndSize(lotRc.minX() + inset, lotRc.minY() + inset, lotRc.width() - 2 * inset, lotRc.height() - 2 * inset);

        int wallHeight = 3;
        int doorWidth = 1;
        int doorHeight = 2;

        Orientation orientation;
        Rect2i doorRc;
        if (r.nextBoolean()) {                               // on the x-axis
            int cx = rc.minX() + (rc.width() - doorWidth) / 2;
            if (r.nextBoolean()) {                           // on the top wall
                int y = rc.minY();
                doorRc = Rect2i.createFromMinAndSize(cx, y, doorWidth, 1);
                orientation = Orientation.NORTH;
            } else {
                int y = rc.minY() + rc.height() - 1;                // on the bottom wall
                doorRc = Rect2i.createFromMinAndSize(cx, y, doorWidth, 1);
                orientation = Orientation.SOUTH;
            }
        } else {
            int cz = rc.minY() + (rc.height() - doorWidth) / 2;
            if (r.nextBoolean()) {                           // on the left wall
                int x = rc.minX();
                doorRc = Rect2i.createFromMinAndSize(x, cz, 1, doorWidth);
                orientation = Orientation.WEST;
            } else {                                         // on the right wall
                int x = rc.minX() + rc.width() - 1;
                doorRc = Rect2i.createFromMinAndSize(x, cz, 1, doorWidth);
                orientation = Orientation.EAST;
            }
        }

        // use door as base height -
        // this is a bit dodgy, because only the first block is considered
        // maybe sample along width of the door and use the average?
        ImmutableVector2i doorDir = orientation.getDir();
        Vector2i probePos = new Vector2i(doorRc.minX() + doorDir.getX(), doorRc.minY() + doorDir.getY());

        // we add +1, because the building starts at 1 block above the terrain
        int baseHeight = TeraMath.floorToInt(hm.getWorld(probePos)) + 1;

        SimpleDoor door = new SimpleDoor(orientation, doorRc, baseHeight, baseHeight + doorHeight);

        // the roof area is 1 block larger all around
        Rect2i roofArea = rc.expand(new Vector2i(1, 1));

        int roofBaseHeight = baseHeight + wallHeight;
        Roof roof = createRoof(r, roofArea, roofBaseHeight);

        SimpleRectHouse bldg = new SimpleRectHouse(orientation, rc, roof, baseHeight, wallHeight);
        bldg.getRoom().addDoor(door);

        for (int i = 0; i < 3; i++) {
            // use the other three cardinal directions to place windows
            Orientation orient = door.getOrientation().getRotated(90 * (i + 1));
            Set<SimpleWindow> wnds = createWindows(rc, baseHeight, orient);

            for (SimpleWindow wnd : wnds) {
                // test if terrain outside window is lower than window base height
                ImmutableVector2i wndDir = wnd.getOrientation().getDir();
                Rect2i wndRect = wnd.getRect();
                Vector2i probePosWnd = new Vector2i(wndRect.minX() + wndDir.getX(), wndRect.minY() + wndDir.getY());
                if (wnd.getBaseHeight() > hm.getWorld(probePosWnd)) {
                    bldg.getRoom().addWindow(wnd);
                }
            }
        }

        return bldg;
    }

    private Set<SimpleWindow> createWindows(Rect2i rc, int baseHeight, Orientation o) {

        final int wndBase = baseHeight + 1;
        final int wndTop = wndBase + 1;
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
            Rect2i rect = Rect2i.createFromMinAndSize(x, border.minY(), wndSize, 1);
            SimpleWindow w = new SimpleWindow(o, rect, wndBase, wndTop);
            result.add(w);
        }

        int firstY = border.minY() + endDist;
        int lastY = border.minY() + border.height() - endDist * 2;

        for (int y = firstY; y <= lastY; y += step) {
            Rect2i rect = Rect2i.createFromMinAndSize(border.minX(), y, 1, wndSize);
            SimpleWindow w = new SimpleWindow(o, rect, wndBase, wndTop);
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
