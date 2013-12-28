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

package org.terasology.cities.raster;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import org.terasology.cities.BlockTypes;
import org.terasology.cities.common.LineUtilities;
import org.terasology.cities.terrain.ConstantHeightMap;
import org.terasology.cities.terrain.HeightMap;
import org.terasology.cities.terrain.OffsetHeightMap;
import org.terasology.math.TeraMath;

/**
 * Converts model elements into blocks
 * @author Martin Steiger
 */
public abstract class Brush {

    /**
     * @param shape the shape to fill
     * @param hmBottom the bottom height map (inclusive)
     * @param height the height of the shape
     * @param type the block type
     */
    public void fillShape(Shape shape, HeightMap hmBottom, int height, BlockTypes type) {
        // optimize, if necessary
       fillShape(shape, hmBottom, new OffsetHeightMap(hmBottom, height), type);
    }
    
    /**
     * @param shape the shape to fill
     * @param hmBottom the bottom height map (inclusive)
     * @param hmTop top height map (exclusive)
     * @param type the block type
     */
    public void fillShape(Shape shape, HeightMap hmBottom, HeightMap hmTop, BlockTypes type) {

        if (!shape.intersects(getAffectedArea())) {
            return;
        }
        
        Rectangle rc = getIntersectionArea(shape.getBounds());

        for (int z = rc.y; z < rc.y + rc.height; z++) {
            for (int x = rc.x; x < rc.x + rc.width; x++) {
            
                if (shape.contains(x, z)) {
                    int y1 = hmBottom.apply(x, z);
                    int y2 = hmTop.apply(x, z);
                    
                    for (int y = y1; y < y2; y++) {
                        setBlock(x, y, z, type);
                    }
                }
            }
        }        
    }

    /**
     * @param x1 left x coord
     * @param x2 right x coord
     * @param z the z coord
     * @param bottom the bottom height (inclusive)
     * @param top the top height of the shape (exclusive)
     * @param type the block type
     */
    public void createWallX(int x1, int x2, int z, int bottom, int top, BlockTypes type) {
        Rectangle rect = new Rectangle(x1, z, x2 - x1, 1);
        HeightMap hmBottom = new ConstantHeightMap(bottom);
        HeightMap hmTop = new ConstantHeightMap(top);
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
        Rectangle rect = new Rectangle(x, z1, 1, z2 - z1);
        ConstantHeightMap hmBottom = new ConstantHeightMap(bottom);
        ConstantHeightMap hmTop = new ConstantHeightMap(top);
        fillRect(rect, hmBottom, hmTop, type);
    }

    /**
     * Fills a rectangle with 1 block thickness
     * @param shape the rectangle
     * @param heightMap the height map
     * @param type the block type
     */
    public void fillRect(Rectangle shape, HeightMap heightMap, BlockTypes type) {
        fillRect(shape, heightMap, new OffsetHeightMap(heightMap, 1), type);        
    }

    /**
     * @param rect the shape to fill
     * @param bottom the bottom height (inclusive)
     * @param top the top height of the shape (exclusive)
     * @param type the block type
     */
    public void fillRect(Rectangle rect, int bottom, int top, BlockTypes type) {
        ConstantHeightMap hmBottom = new ConstantHeightMap(bottom);
        ConstantHeightMap hmTop = new ConstantHeightMap(top);
        fillRect(rect, hmBottom, hmTop, type);
    }

    /**
     * @param rect the shape to fill
     * @param hmBottom the bottom height map (inclusive)
     * @param height the top height of the shape (exclusive)
     * @param type the block type
     */
    public void fillRect(Rectangle rect, HeightMap hmBottom, int height, BlockTypes type) {
        // optimize, if necessary
        fillRect(rect, hmBottom, new ConstantHeightMap(height), type);
    }

    /**
     * @param rect the shape to fill
     * @param baseHeight the bottom height (inclusive)
     * @param hmTop the top height map (exclusive) 
     * @param type the block type
     */
    public void fillRect(Rectangle rect, int baseHeight, HeightMap hmTop, BlockTypes type) {
        // optimize, if necessary
        fillRect(rect, new ConstantHeightMap(baseHeight), hmTop, type);
    }
    
    /**
     * @param rect the area to fill
     * @param hmBottom the height map at the bottom (inclusive)
     * @param hmTop the height map for the top (exclusive)
     * @param type the block type
     */
    public void fillRect(Rectangle rect, HeightMap hmBottom, HeightMap hmTop, BlockTypes type) {
        Rectangle rc = getIntersectionArea(rect);

        if (rc.isEmpty()) {
            return;
        }

        for (int z = rc.y; z < rc.y + rc.height; z++) {
            for (int x = rc.x; x < rc.x + rc.width; x++) {

                int y1 = hmBottom.apply(x, z);
                int y2 = hmTop.apply(x, z);

                for (int y = y1; y < y2; y++) {
                    setBlock(x, y, z, type);
                }
            }
        }
    }
    /**
     * @param shape the shape to test
     * @return true if the shape can be affected by this brush
     */
    public boolean affects(Shape shape) {
        return shape.intersects(getAffectedArea());
    }

    /**
     * @param x x in world coords
     * @param y y in world coords
     * @param z z in world coords
     * @param type the block type
     */
    public abstract void setBlock(int x, int y, int z, BlockTypes type);
    
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
    public abstract Rectangle getAffectedArea();

    /**
     * @param rect the rectangle that should be drawn
     * @return the intersection between the brush area and the rectangle
     */
    public Rectangle getIntersectionArea(Rectangle rect) {
        return getAffectedArea().intersection(rect);
    }

    /**
     * @param rc the outline rect
     * @param baseHeight the base height
     * @param topHeight the top height
     * @param type the block type
     */
    public void frame(Rectangle rc, int baseHeight, int topHeight, BlockTypes type) {
        
        // walls along z-axis
        createWallZ(rc.y, rc.y + rc.height, rc.x, baseHeight, topHeight, type);
        createWallZ(rc.y, rc.y + rc.height, rc.x + rc.width - 1, baseHeight, topHeight, type);

        // walls along x-axis
        createWallX(rc.x, rc.x + rc.width, rc.y, baseHeight, topHeight, type);
        createWallX(rc.x, rc.x + rc.width, rc.y + rc.height - 1, baseHeight, topHeight, type);
        
    }
    
    /**
     * Draws a line.<br/>
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
        
        Rectangle outerBox = getAffectedArea();
        double shrink = 0.01;
        Rectangle2D area = new Rectangle2D.Double(outerBox.x, outerBox.y, outerBox.width - shrink, outerBox.height - shrink);
        
        Line2D line = new Line2D.Double(x1, z1, x2, z2);
        if (LineUtilities.clipLine(line, area)) {
            int cx1 = TeraMath.floorToInt(line.getX1());
            int cy1 = TeraMath.floorToInt(line.getY1());
            int cx2 = TeraMath.floorToInt(line.getX2());
            int cy2 = TeraMath.floorToInt(line.getY2());
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
}
