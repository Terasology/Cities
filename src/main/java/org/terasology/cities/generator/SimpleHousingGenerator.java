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
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import org.terasology.cities.model.SimpleLot;
import org.terasology.cities.model.bldg.SimpleBuilding;
import org.terasology.cities.model.bldg.SimpleDoor;
import org.terasology.cities.model.bldg.SimpleHome;
import org.terasology.cities.model.bldg.SimpleWindow;
import org.terasology.cities.model.roof.DomeRoof;
import org.terasology.cities.model.roof.HipRoof;
import org.terasology.cities.model.roof.Roof;
import org.terasology.cities.model.roof.SaddleRoof;
import org.terasology.commonworld.Orientation;
import org.terasology.commonworld.geom.Rectangles;
import org.terasology.math.Vector2i;
import org.terasology.utilities.random.MersenneRandom;
import org.terasology.utilities.random.Random;

import com.google.common.base.Function;
import com.google.common.collect.Sets;

/**
 * A simple building generator. It places a {@link SimpleHome}s in 
 * the center of the given lot.  
 */
public class SimpleHousingGenerator implements Function<SimpleLot, Set<SimpleBuilding>> {

    private final String seed;
    private Function<Vector2i, Integer> heightMap;
    
    /**
     * @param seed the random seed
     * @param heightMap the terrain height function
     */
    public SimpleHousingGenerator(String seed, Function<Vector2i, Integer> heightMap) {
        this.seed = seed;
        this.heightMap = heightMap;
    }

    /**
     * @param lot the lot
     * @return a single building
     */
    @Override
    public Set<SimpleBuilding> apply(SimpleLot lot) {
        // leave 1 block border for the building
        Rectangle lotRc = lot.getShape();
        
        // use the rectangle, not the lot itself, because its hashcode is the identity hashcode
        Random r = new MersenneRandom(Objects.hash(seed, lotRc));
        
        int inset = 2;
        Rectangle rc = new Rectangle(lotRc.x + inset, lotRc.y + inset, lotRc.width - 2 * inset, lotRc.height - 2 * inset);
        
        int wallHeight = 3;
        int doorWidth = 1;
        int doorHeight = 2;
        
        Orientation orientation;
        Rectangle doorRc;
        if (r.nextBoolean()) {                               // on the x-axis
            int cx = rc.x + (rc.width - doorWidth) / 2; 
            if (r.nextBoolean()) {                           // on the top wall
                int y = rc.y;
                doorRc = new Rectangle(cx, y, doorWidth, 1);
                orientation = Orientation.NORTH;
            } else {
                int y = rc.y + rc.height - 1;                // on the bottom wall
                doorRc = new Rectangle(cx, y, doorWidth, 1);
                orientation = Orientation.SOUTH;
            }
        } else {
            int cz = rc.y + (rc.height - doorWidth) / 2; 
            if (r.nextBoolean()) {                           // on the left wall
                int x = rc.x;
                doorRc = new Rectangle(x, cz, 1, doorWidth);
                orientation = Orientation.WEST;
            } else {                                         // on the right wall
                int x = rc.x + rc.width - 1;
                doorRc = new Rectangle(x, cz, 1, doorWidth);
                orientation = Orientation.EAST;
            }
        }
        
        // use door as base height - 
        // this is a bit dodgy, because only the first block is considered
        // maybe sample along width of the door and use the average?
        Vector2i doorDir = orientation.getDir();
        Vector2i probePos = new Vector2i(doorRc.x + doorDir.x, doorRc.y + doorDir.y);

        // we add +1, because the building starts at 1 block above the terrain
        int baseHeight = heightMap.apply(probePos) + 1;

        SimpleDoor door = new SimpleDoor(orientation, doorRc, baseHeight, baseHeight + doorHeight);

        // the roof area is 1 block larger all around
        Rectangle roofArea = Rectangles.expandRect(rc, 1);

        int roofBaseHeight = baseHeight + wallHeight;
        Roof roof = createRoof(r, roofArea, roofBaseHeight);

        SimpleHome bldg = new SimpleHome(rc, roof, baseHeight, wallHeight, door);
        
        for (int i = 0; i < 3; i++) {
            // use the other three cardinal directions to place windows
            Orientation orient = door.getOrientation().getRotated(90 * (i + 1));
            Set<SimpleWindow> wnds = createWindows(rc, baseHeight, orient);
            
            for (SimpleWindow wnd : wnds) {
                // test if terrain outside window is lower than window base height
                Vector2i wndDir = wnd.getOrientation().getDir();
                Rectangle wndRect = wnd.getRect();
                Vector2i probePosWnd = new Vector2i(wndRect.x + wndDir.x, wndRect.y + wndDir.y);
                if (wnd.getBaseHeight() > heightMap.apply(probePosWnd)) {
                    bldg.addWindow(wnd);
                }
            }
        }
        
        return Collections.<SimpleBuilding>singleton(bldg);
    }

    private Set<SimpleWindow> createWindows(Rectangle rc, int baseHeight, Orientation o) {
        
        final int wndBase = baseHeight + 1;
        final int wndTop = wndBase + 1;
        final int endDist = 2;
        final int interDist = 2;
        final int wndSize = 1;

        Set<SimpleWindow> result = Sets.newHashSet();
        
        Rectangle border = Rectangles.getBorder(rc, o);
        int step = interDist + wndSize;
        
        int firstX = border.x + endDist;
        int lastX = border.x + border.width - endDist * 2;
        
        for (int x = firstX; x <= lastX; x += step) {
            Rectangle rect = new Rectangle(x, border.y, wndSize, 1);
            SimpleWindow w = new SimpleWindow(o, rect, wndBase, wndTop);
            result.add(w);
        }
        
        int firstY = border.y + endDist;
        int lastY = border.y + border.height - endDist * 2;
        
        for (int y = firstY; y <= lastY; y += step) {
            Rectangle rect = new Rectangle(border.x, y, 1, wndSize);
            SimpleWindow w = new SimpleWindow(o, rect, wndBase, wndTop);
            result.add(w);
        }
        
        return result;
    }

    private Roof createRoof(Random r, Rectangle roofArea, int roofBaseHeight) {
        int type = r.nextInt(100);
        
        if (type < 33) {
            int roofPitch = 1;
            return new HipRoof(roofArea, roofBaseHeight, roofPitch, roofBaseHeight + 1);
        }
        
        if (type < 66) {
            return new DomeRoof(roofArea, roofBaseHeight, Math.min(roofArea.width, roofArea.height) / 2);
        }

        boolean alongX = (roofArea.width > roofArea.height);
        Orientation o = alongX ? Orientation.EAST : Orientation.NORTH;
        
        return new SaddleRoof(roofArea, roofBaseHeight, o, 1);
    }
}
