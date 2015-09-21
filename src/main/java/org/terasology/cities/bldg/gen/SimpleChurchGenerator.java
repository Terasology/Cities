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

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.math.RoundingMode;



import org.terasology.cities.BlockTypes;
import org.terasology.cities.bldg.RectBuildingPart;
import org.terasology.cities.bldg.SimpleChurch;
import org.terasology.cities.bldg.WingDoor;
import org.terasology.cities.common.Edges;
import org.terasology.cities.model.roof.HipRoof;
import org.terasology.cities.model.roof.PentRoof;
import org.terasology.cities.model.roof.SaddleRoof;
import org.terasology.cities.parcels.Parcel;
import org.terasology.cities.surface.InfiniteSurfaceHeightFacet;
import org.terasology.cities.window.RectWindow;
import org.terasology.commonworld.Orientation;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.ImmutableVector2i;
import org.terasology.math.geom.LineSegment;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.utilities.random.MersenneRandom;
import org.terasology.utilities.random.Random;

/**
 * Creates {@link SimpleChurch}es
 */
public class SimpleChurchGenerator {

    private final long seed;

    /**
     * @param seed the seed
     */
    public SimpleChurchGenerator(long seed) {
        this.seed = seed;
    }

    /**
     * @param lot the lot to use
     * @return a generated {@link SimpleChurch} model
     */
    public SimpleChurch apply(Parcel lot, InfiniteSurfaceHeightFacet hm) {

        Random rand = new MersenneRandom(seed ^ lot.getShape().hashCode());

        // make build-able area 1 block smaller, so make the roof stay inside
        Rect2i lotRc = lot.getShape().expand(new Vector2i(-1, -1));

        boolean alignEast = (lotRc.width() > lotRc.height());

        // build along larger axis and rotate later, if necessary
        int width = Math.max(lotRc.width(), lotRc.height());
        int height = Math.min(lotRc.width(), lotRc.height());

        int sideOff = 3;
        int sideWidth = 5;
        int entranceWidth = 2;
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
        Rect2i naveRect = Rect2i.createFromMinAndSize(0, ny, naveLen, naveWidth);
        Rect2i towerRect = Rect2i.createFromMinAndSize(naveLen - 1, ty, towerSize, towerSize); // -1 makes them overlap
        Rect2i doorRc = Rect2i.createFromMinAndSize(0, dy, 1, entranceWidth);
        Orientation doorOrientation = Orientation.WEST;

        Rect2i aisleLeftRc = Rect2i.createFromMinAndSize(sideOff, ny - sideWidth + 1, naveLen - 2 * sideOff, sideWidth);    // make them overlap
        Rect2i aisleRightRc = Rect2i.createFromMinAndSize(sideOff, ny + naveWidth - 1, naveLen - 2 * sideOff, sideWidth);   // make them overlap

        int rot = alignEast ? 0 : 90;

        if (rand.nextBoolean()) {
            rot += 180;
        }

        Point center = new Point(width / 2, height / 2);
        naveRect = transformRect(naveRect, lotRc, center, rot);
        towerRect = transformRect(towerRect, lotRc, center, rot);
        doorRc = transformRect(doorRc, lotRc, center, rot);
        aisleLeftRc = transformRect(aisleLeftRc, lotRc, center, rot);
        aisleRightRc = transformRect(aisleRightRc, lotRc, center, rot);
        doorOrientation = doorOrientation.getRotated(rot);
        Orientation leftOrient = Orientation.SOUTH.getRotated(rot);
        Orientation rightOrient = Orientation.NORTH.getRotated(rot);

        ImmutableVector2i doorDir = doorOrientation.getDir();
        Rect2i probeRc = Rect2i.createFromMinAndSize(doorRc.minX() + doorDir.getX(), doorRc.minY() + doorDir.getY(), doorRc.width(), doorRc.height());

        int baseHeight = getMaxHeight(probeRc, hm) + 1; // 0 == terrain
        int towerHeight = 22;
        int hallHeight = 9;
        int sideHeight = 4;

        Rect2i towerRoofRect = towerRect.expand(1, 1);
        HipRoof towerRoof = new HipRoof(towerRoofRect, baseHeight + towerHeight, 2);
        RectBuildingPart tower = new RectBuildingPart(towerRect, towerRoof, baseHeight, towerHeight);

        Rect2i naveRoofRect = naveRect.expand(1, 1);
        WingDoor entrance = new WingDoor(doorOrientation, doorRc, baseHeight, baseHeight + entranceHeight);
        SaddleRoof naveRoof = new SaddleRoof(naveRoofRect, baseHeight + hallHeight, entrance.getOrientation(), 1);
        RectBuildingPart nave = new RectBuildingPart(naveRect, naveRoof, baseHeight, hallHeight);
        nave.addDoor(entrance);

        SimpleChurch church = new SimpleChurch(doorOrientation, nave, tower);

        PentRoof roofLeft = new PentRoof(aisleLeftRc.expand(1, 1), baseHeight + sideHeight, leftOrient, 0.5);
        PentRoof roofRight = new PentRoof(aisleRightRc.expand(1, 1), baseHeight + sideHeight, rightOrient, 0.5);
        RectBuildingPart aisleLeft = new RectBuildingPart(aisleLeftRc, roofLeft, baseHeight, sideHeight);
        RectBuildingPart aisleRight = new RectBuildingPart(aisleRightRc, roofRight, baseHeight, sideHeight);

        church.addPart(aisleLeft);
        church.addPart(aisleRight);

        // create and add tower windows
        for (int i = 0; i < 3; i++) {
            // use the other three cardinal directions to place windows
            Orientation orient = entrance.getOrientation().getRotated(90 * (i + 1));
            LineSegment towerBorder = Edges.getEdge(towerRect, orient);
            Vector2i towerPos = new Vector2i(towerBorder.lerp(0.5f), RoundingMode.HALF_UP);

            Rect2i wndRect = Rect2i.createFromMinAndSize(towerPos.getX(), towerPos.getY(), 1, 1);
            tower.addWindow(new RectWindow(orient, wndRect, baseHeight + towerHeight - 3, baseHeight + towerHeight - 1, BlockTypes.AIR));
        }

        return church;
    }

    /**
     * @param rc the rectangle to transform
     * @param bounds the bounding rectangle for the transformation (translation offset and rotation center)
     * @param center the center of the original placing rect
     * @param rot the rotation in degrees (only multiples of 45deg.)
     * @return the translated and rotated rectangle
     */
    public static Rect2i transformRect(Rect2i rc, Rect2i bounds, Point center, int rot) {

        double anchorx = bounds.width() * 0.5;
        double anchory = bounds.height() * 0.5;

        AffineTransform at = new AffineTransform();
        at.translate(bounds.minX(), bounds.minY());
        at.translate(anchorx, anchory);
        at.rotate(Math.toRadians(rot));
        at.translate(-center.x, -center.y);

        Point ptSrc1 = new Point(rc.minX(), rc.minY());
        Point ptSrc2 = new Point(rc.minX() + rc.width(), rc.minY() + rc.height());
        Point ptDst1 = new Point();
        Point ptDst2 = new Point();
        at.transform(ptSrc1, ptDst1);
        at.transform(ptSrc2, ptDst2);

        int x = Math.min(ptDst1.x, ptDst2.x);
        int y = Math.min(ptDst1.y, ptDst2.y);
        int width = Math.max(ptDst1.x, ptDst2.x) - x;
        int height = Math.max(ptDst1.y, ptDst2.y) - y;
        Rect2i result = Rect2i.createFromMinAndSize(x, y, width, height);
        return result;
    }

    private int getMaxHeight(Rect2i rc, InfiniteSurfaceHeightFacet hm) {
        int maxHeight = Integer.MIN_VALUE;

        for (int z = rc.minY(); z <= rc.maxY(); z++) {
            for (int x = rc.minX(); x <= rc.maxX(); x++) {
                int height = TeraMath.floorToInt(hm.getWorld(x, z));
                if (maxHeight < height) {
                    maxHeight = height;
                }
            }
        }

        return maxHeight;
    }
}
