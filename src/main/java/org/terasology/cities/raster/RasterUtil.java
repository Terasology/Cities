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

package org.terasology.cities.raster;

import org.terasology.math.TeraMath;
import org.terasology.math.geom.LineSegment;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2f;

/**
 * Converts model elements into blocks
 */
public abstract class RasterUtil {

//    /**
//     * @param shape the shape to fill
//     * @param hmBottom the bottom height map (inclusive)
//     * @param hmTop top height map (exclusive)
//     * @param type the block type
//     */
//    public static void fillShape(Shape shape, HeightMap hmBottom, HeightMap hmTop, BlockTypes type) {
//
//        if (!getAffectedArea().overlaps(shape.getBounds()) {
//            return;
//        }
//
//        Rect2i rc = getIntersectionArea(shape.getBounds());
//
//        for (int z = rc.minY(); z <= rc.maxY(); z++) {
//            for (int x = rc.minX(); x <= rc.maxX(); x++) {
//
//                if (shape.contains(x, z)) {
//                    int y1 = hmBottom.apply(x, z);
//                    int y2 = hmTop.apply(x, z);
//
//                    for (int y = y1; y < y2; y++) {
//                        setBlock(x, y, z, type);
//                    }
//                }
//            }
//        }
//    }

    /**
     * if (x2 < x1) nothing will be drawn.
     * @param pen the pen use
     * @param x1 left x coord
     * @param x2 right x coord
     * @param z the z coord
     */
    public static void drawLineX(Pen pen, int x1, int x2, int z) {
        Rect2i rc = pen.getTargetArea();

        if (z >= rc.minY() && z <= rc.maxY()) {
            int minX = Math.max(x1, rc.minX());
            int maxX = Math.min(x2, rc.maxX());
            for (int x = minX; x <= maxX; x++) {
                pen.draw(x, z);
            }
        }
    }

    /**
     * if (z2 < z1) nothing will be drawn.
     * @param pen the pen use
     * @param z1 top z coord
     * @param z2 bottom z coord
     * @param x the x coord
     */
    public static void drawLineZ(Pen pen, int x, int z1, int z2) {
        Rect2i rc = pen.getTargetArea();

        if (x >= rc.minX() && x <= rc.maxX()) {
            int minZ = Math.max(z1, rc.minY());
            int maxZ = Math.min(z2, rc.maxY());
            for (int z = minZ; z <= maxZ; z++) {
                pen.draw(x, z);
            }
        }
    }

    /**
     * @param rect the area to fill
     * @param pen the pen to use for the rasterization of the rectangle
     */
    public static void fillRect(Pen pen, Rect2i rect) {
        Rect2i rc = pen.getTargetArea().intersect(rect);

        if (rc.isEmpty()) {
            return;
        }

        for (int z = rc.minY(); z <= rc.maxY(); z++) {
            for (int x = rc.minX(); x <= rc.maxX(); x++) {
                pen.draw(x, z);
            }
        }
    }

    /**
     * @param pen the pen to use
     * @param rc the rectangle to draw
     */
    public static void drawRect(Pen pen, Rect2i rc) {

        // walls along x-axis
        drawLineX(pen, rc.minX(), rc.maxX(), rc.minY());
        drawLineX(pen, rc.minX(), rc.maxX(), rc.maxY());

        // walls along z-axis
        drawLineZ(pen, rc.minX(), rc.minY() + 1, rc.maxY() - 1); // no need to draw corners again
        drawLineZ(pen, rc.maxX(), rc.minY() + 1, rc.maxY() - 1); //  -> inset by one on both ends

    }

    /**
     * Draws a line.<br>
     * See Wikipedia: Bresenham's line algorithm, chapter Simplification
     * @param pen the pen to use
     * @param line the line to draw
     */
    public static void drawLine(Pen pen, LineSegment line) {

        Rect2i outerBox = pen.getTargetArea();

        Vector2f p0 = new Vector2f();
        Vector2f p1 = new Vector2f();
        if (line.getClipped(outerBox, p0, p1)) {
            int cx1 = TeraMath.floorToInt(p0.getX());
            int cy1 = TeraMath.floorToInt(p0.getY());
            int cx2 = TeraMath.floorToInt(p1.getX());
            int cy2 = TeraMath.floorToInt(p1.getY());
            drawClippedLine(pen, cx1, cy1, cx2, cy2);
        }
    }


    private static void drawClippedLine(Pen pen, int x1, int z1, int x2, int z2) {

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(z2 - z1);

        int sx = (x1 < x2) ? 1 : -1;
        int sy = (z1 < z2) ? 1 : -1;

        int err = dx - dy;

        int x = x1;
        int z = z1;

        while (true) {
            pen.draw(x, z);

            if (x == x2 && z == z2) {
                break;
            }

            int e2 = 2 * err;

            if (e2 > -dy) {
                err = err - dy;
                x += sx;
            }
            // if going along diagonals is not ok use " .. } else if (e2.. " instead

            if (e2 < dx) {
                err = err + dx;
                z += sy;
            }
        }
    }

    /**
     * Draws a circle based on Horn's algorithm (see B. K. P. Horn: Circle Generators for Display Devices.
     * Computer Graphics and Image Processing 5, 2 - June 1976)
     * @param cx the center x
     * @param cy the center y
     * @param rad the radius
     * @param checkedPen the receiving instance. Must be checked, because iterator could draw outside.
     */
    public static void drawCircle(CheckedPen checkedPen, int cx, int cy, int rad) {
        int d = -rad;
        int x = rad;
        int y = 0;
        while (y <= x) {
            checkedPen.draw(cx + x, cy + y);
            checkedPen.draw(cx - x, cy + y);
            checkedPen.draw(cx - x, cy - y);
            checkedPen.draw(cx + x, cy - y);

            checkedPen.draw(cx + y, cy + x);
            checkedPen.draw(cx - y, cy + x);
            checkedPen.draw(cx - y, cy - x);
            checkedPen.draw(cx + y, cy - x);

            d = d + 2 * y + 1;
            y = y + 1;
            if (d > 0) {
                d = d - 2 * x + 2;
                x = x - 1;
            }
        }
    }

    /**
     * @param cx the center x
     * @param cy the center y
     * @param rad the radius
     * @param pen the pen to draw
     */
    public static void fillCircle(CheckedPen pen, int cx, int cy, int rad) {
        for (int y = 0; y <= rad; y++) {
            for (int x = 0; x * x + y * y <= (rad + 0.5) * (rad + 0.5); x++) {
                pen.draw(cx + x, cy + y);
                pen.draw(cx - x, cy + y);
                pen.draw(cx - x, cy - y);
                pen.draw(cx + x, cy - y);
            }
        }
    }
}
