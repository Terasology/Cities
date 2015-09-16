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

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Set;

import org.terasology.cities.BlockTypes;
import org.terasology.commonworld.geom.LineUtilities;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.commonworld.heightmap.HeightMaps;
import org.terasology.math.Side;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.ImmutableVector2f;
import org.terasology.math.geom.LineSegment;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Shape;
import org.terasology.math.geom.Vector2f;

/**
 * Converts model elements into blocks
 */
public abstract class Brush {

//    /**
//     * @param shape the shape to fill
//     * @param hmBottom the bottom height map (inclusive)
//     * @param hmTop top height map (exclusive)
//     * @param type the block type
//     */
//    public void fillShape(Shape shape, HeightMap hmBottom, HeightMap hmTop, BlockTypes type) {
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
     * @param x1 left x coord
     * @param x2 right x coord
     * @param z the z coord
     * @param bottom the bottom height (inclusive)
     * @param top the top height of the shape (exclusive)
     * @param type the block type
     */
    public void createWallX(int x1, int x2, int z, int bottom, int top, BlockTypes type) {
        Rect2i rect = Rect2i.createFromMinAndSize(x1, z, x2 - x1, 1);
        HeightMap hmBottom = HeightMaps.constant(bottom);
        HeightMap hmTop = HeightMaps.constant(top);
        fillRect(rect, hmBottom, hmTop, type);
    }

    /**
     * @param x the x coord
     * @param z1 left z coord
     * @param z2 right z coord
     * @param bottom the bottom height (inclusive)
     * @param top the top height of the shape
     * @param type the block type
     */
    public void createWallZ(int z1, int z2, int x, int bottom, int top, BlockTypes type) {
        Rect2i rect = Rect2i.createFromMinAndSize(x, z1, 1, z2 - z1);
        HeightMap hmBottom = HeightMaps.constant(bottom);
        HeightMap hmTop = HeightMaps.constant(top);
        fillRect(rect, hmBottom, hmTop, type);
    }

    /**
     * Fills a rectangle with 1 block thickness
     * @param shape the rectangle
     * @param heightMap the height map
     * @param type the block type
     */
    public void fillRect(Rect2i shape, HeightMap heightMap, BlockTypes type) {
        fillRect(shape, heightMap, HeightMaps.offset(heightMap, 1), type);
    }

    /**
     * @param rect the shape to fill
     * @param bottom the bottom height (inclusive)
     * @param top the top height of the shape (exclusive)
     * @param type the block type
     */
    public void fillRect(Rect2i rect, int bottom, int top, BlockTypes type) {
        HeightMap hmBottom = HeightMaps.constant(bottom);
        HeightMap hmTop = HeightMaps.constant(top);
        fillRect(rect, hmBottom, hmTop, type);
    }

    /**
     * @param rect the shape to fill
     * @param hmBottom the bottom height map (inclusive)
     * @param height the top height of the shape (exclusive)
     * @param type the block type
     */
    public void fillRect(Rect2i rect, HeightMap hmBottom, int height, BlockTypes type) {
        // optimize, if necessary
        fillRect(rect, hmBottom, HeightMaps.constant(height), type);
    }

    /**
     * @param rect the shape to fill
     * @param baseHeight the bottom height (inclusive)
     * @param hmTop the top height map (exclusive)
     * @param type the block type
     */
    public void fillRect(Rect2i rect, int baseHeight, HeightMap hmTop, BlockTypes type) {
        // optimize, if necessary
        fillRect(rect, HeightMaps.constant(baseHeight), hmTop, type);
    }

    /**
     * @param rect the area to fill
     * @param hmBottom the height map at the bottom (inclusive)
     * @param hmTop the height map for the top (exclusive)
     * @param type the block type
     */
    public void fillRect(Rect2i rect, HeightMap hmBottom, HeightMap hmTop, BlockTypes type) {
        Rect2i rc = getIntersectionArea(rect);

        if (rc.isEmpty()) {
            return;
        }

        for (int z = rc.minY(); z <= rc.maxY(); z++) {
            for (int x = rc.minX(); x <= rc.maxX(); x++) {

                int y1 = hmBottom.apply(x, z);
                int y2 = hmTop.apply(x, z);

                for (int y = y1; y < y2; y++) {
                    setBlock(x, y, z, type);
                }
            }
        }
    }

    /**
     * @param x x in world coords
     * @param y y in world coords
     * @param z z in world coords
     * @param type the block type
     */
    public abstract void setBlock(int x, int y, int z, BlockTypes type);

    /**
     * @param x x in world coords
     * @param y y in world coords
     * @param z z in world coords
     * @param type the block type
     * @param side the side (used to find the right block from the family)
     */
    public abstract void setBlock(int x, int y, int z, BlockTypes type, Set<Side> side);

    /**
     * @return the maximum drawing height
     */
    public abstract int getMaxHeight();

    /**
     * @return the maximum drawing height
     */
    public abstract int getMinHeight();

    /**
     * @return the area that is drawn by this brush
     */
    public abstract Rect2i getAffectedArea();

    /**
     * @param rect the rectangle that should be drawn
     * @return the intersection between the brush area and the rectangle
     */
    public Rect2i getIntersectionArea(Rect2i rect) {
        return getAffectedArea().intersect(rect);
    }

    /**
     * @param rc the outline rect
     * @param baseHeight the base height
     * @param topHeight the top height
     * @param type the block type
     */
    public void frame(Rect2i rc, int baseHeight, int topHeight, BlockTypes type) {

        // walls along z-axis
        createWallZ(rc.minY(), rc.minY() + rc.height(), rc.minX(), baseHeight, topHeight, type);
        createWallZ(rc.minY(), rc.minY() + rc.height(), rc.minX() + rc.width() - 1, baseHeight, topHeight, type);

        // walls along x-axis
        createWallX(rc.minX(), rc.minX() + rc.width(), rc.minY(), baseHeight, topHeight, type);
        createWallX(rc.minX(), rc.minX() + rc.width(), rc.minY() + rc.height() - 1, baseHeight, topHeight, type);

    }

    /**
     * Draws a line.<br>
     * See Wikipedia: Bresenham's line algorithm, chapter Simplification
     * @param x1 x start in world coords
     * @param z1 z start in world coords
     * @param x2 x end in world coords
     * @param z2 z end in world coords
     * @param hmBottom the height map at the bottom (inclusive)
     * @param hmTop the height map for the top (exclusive)
     * @param type the block type
     */
    public void draw(HeightMap hmBottom, HeightMap hmTop, int x1, int z1, int x2, int z2, BlockTypes type) {

        Rect2i outerBox = getAffectedArea();

        LineSegment line = new LineSegment(x1, z1, x2, z2);
        Vector2f p0 = new Vector2f();
        Vector2f p1 = new Vector2f();
        if (line.getClipped(outerBox, p0, p1)) {
            int cx1 = TeraMath.floorToInt(p0.getX());
            int cy1 = TeraMath.floorToInt(p0.getY());
            int cx2 = TeraMath.floorToInt(p1.getX());
            int cy2 = TeraMath.floorToInt(p1.getY());
            drawClippedLine(hmBottom, hmTop, cx1, cy1, cx2, cy2, type);
        }
    }


    private void drawClippedLine(HeightMap hmBottom, HeightMap hmTop, int x1, int z1, int x2, int z2, BlockTypes type) {

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(z2 - z1);

        int sx = (x1 < x2) ? 1 : -1;
        int sy = (z1 < z2) ? 1 : -1;

        int err = dx - dy;

        int x = x1;
        int z = z1;

        while (true) {
            for (int y = hmBottom.apply(x, z); y < hmTop.apply(x, z); y++) {
                setBlock(x, y, z, type);
            }

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
     * Horn's algorithm B. K. P. Horn: Circle Generators for Display Devices.
     * Computer Graphics and Image Processing 5, 2 (June 1976)
     * @param cx the center x
     * @param cy the center y
     * @param rad the radius
     * @param hm the height map
     * @param type the block type to set
     */
    public void drawCircle(int cx, int cy, int rad, HeightMap hm, BlockTypes type) {
        int d = -rad;
        int x = rad;
        int y = 0;
        while (y <= x) {
            setBlock(cx + x, hm.apply(cx + x, cy + y), cy + y, type);
            setBlock(cx - x, hm.apply(cx - x, cy + y), cy + y, type);
            setBlock(cx - x, hm.apply(cx - x, cy - y), cy - y, type);
            setBlock(cx + x, hm.apply(cx + x, cy - y), cy - y, type);

            setBlock(cx + y, hm.apply(cx + y, cy + x), cy + x, type);
            setBlock(cx - y, hm.apply(cx - y, cy + x), cy + x, type);
            setBlock(cx - y, hm.apply(cx - y, cy - x), cy - x, type);
            setBlock(cx + y, hm.apply(cx + y, cy - x), cy - x, type);

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
    public void fillCircle(int cx, int cy, int rad, Pen pen) {
        for (int y = 0; y <= rad; y++) {
            for (int x = 0; x * x + y * y <= (rad + 0.5) * (rad + 0.5); x++) {
                pen.draw(cx + x, cy + y);
                pen.draw(cx - x, cy + y);
                pen.draw(cx - x, cy - y);
                pen.draw(cx + x, cy - y);
            }
        }
    }

    /**
     * @param cx the center x
     * @param cy the center y
     * @param rad the radius
     * @param hm the height map
     * @param type the block type to set
     */
    public void fillCircle(int cx, int cy, int rad, HeightMap hm, BlockTypes type) {
        for (int y = 0; y <= rad; y++) {
            for (int x = 0; x * x + y * y <= (rad + 0.5) * (rad + 0.5); x++) {
                setBlock(cx + x, hm.apply(cx + x, cy + y), cy + y, type);
                setBlock(cx - x, hm.apply(cx - x, cy + y), cy + y, type);
                setBlock(cx - x, hm.apply(cx - x, cy - y), cy - y, type);
                setBlock(cx + x, hm.apply(cx + x, cy - y), cy - y, type);
            }
        }
    }

}
