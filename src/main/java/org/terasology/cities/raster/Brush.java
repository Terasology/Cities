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
import java.awt.geom.Ellipse2D;

import org.terasology.cities.BlockTypes;
import org.terasology.cities.terrain.HeightMap;
import org.terasology.cities.terrain.OffsetHeightMap;
import org.terasology.math.Vector2i;
import org.terasology.math.Vector3i;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;

import com.google.common.base.Function;

/**
 * Converts model elements into blocks
 * @author Martin Steiger
 */
public abstract class Brush {
    private final HeightMap heightMap;

    /**
     * @param heightMap the height map
     */
    public Brush(HeightMap heightMap) {
        this.heightMap = heightMap;
    }

    public abstract Rectangle getIntersectionArea(int x1, int z1, int x2, int z2);
    
    public abstract int getMaxHeight();

    public Rectangle getIntersectionArea(Vector3i from, Vector3i to) {
        int x1 = from.x;
        int x2 = to.x;
        int z1 = from.z;
        int z2 = to.z;

        return getIntersectionArea(x1, z1, x2, z2);
    }

    public Rectangle getIntersectionArea(Rectangle fullRc) {
        int x1 = fullRc.x;
        int x2 = fullRc.x + fullRc.width;
        int z1 = fullRc.y;
        int z2 = fullRc.y + fullRc.height;

        return getIntersectionArea(x1, z1, x2, z2);
    }

    public void createWallX(int x1, int x2, int z, int baseHeight, int height, String type) {
        fill(new Vector3i(x1, baseHeight, z), new Vector3i(x2, baseHeight + height, z + 1), type);
    }

    public void createWallZ(int z1, int z2, int x, int baseHeight, int height, String type) {
        fill(new Vector3i(x, baseHeight, z1), new Vector3i(x + 1, baseHeight + height, z2), type);
    }

    public void fill(Rectangle rc, int y1, int y2, String type) {
        fill(new Vector3i(rc.x, y1, rc.y), new Vector3i(rc.x + rc.width, y2, rc.y + rc.height), type);
    }

    /**
     * Fill a cuboid with a specified block
     * @param from the starting coordinates (inclusive)
     * @param to the end coordinates (exclusive)
     * @param type the block type to use for filling
     */
    public void fill(Vector3i from, Vector3i to, String type) {
        Rectangle rc = getIntersectionArea(from, to);
        int y1 = from.y;
        int y2 = to.y;
        for (int z = rc.y; z < rc.y + rc.height; z++) {
            for (int x = rc.x; x < rc.x + rc.width; x++) {
                for (int y = y1; y < y2; y++) {
                    setBlock(x, y, z, type);
                }
            }
        }
    }

    /**
     * Fills all air block below a given height level with the specified String type
     * @param fullRc the area to clear
     * @param y1 the base height to start
     * @param type the block type that is used for filling
     */
    public void fillAirBelow(Rectangle fullRc, int y1, String type) {
        Rectangle rc = getIntersectionArea(fullRc);

        for (int z = rc.y; z < rc.y + rc.height; z++) {
            for (int x = rc.x; x < rc.x + rc.width; x++) {

                // starting from the top, we go down as long as we encounter air
                for (int y = y1; y >= 0; y--) {
                    if (!isAir(x, y, z)) {
                        break;
                    }

                    setBlock(x, y, z, type);
                }
            }
        }
    }

    /**
     * Removes all blocks above a defined height level
     * @param fullRc the area to clear
     * @param y1 the base height to start
     */
    public void clearAbove(Rectangle fullRc, int y1) {
        Rectangle rc = getIntersectionArea(fullRc);

        for (int z = rc.y; z < rc.y + rc.height; z++) {
            for (int x = rc.x; x < rc.x + rc.width; x++) {

                // starting from the bottom, we go up until we hit air
                for (int y = y1; y < getMaxHeight(); y++) {
                    if (isAir(x, y, z)) {
                        break;
                    }

                    setBlock(x, y, z, "air");        // BlockManager.getAir()
                }
            }
        }
    }

    public void setBlockOnTerrain(int x, int z, String block) {
        int y = heightMap.apply(new Vector2i(x, z));
        setBlock(x, y, z, block);
    }
        
    /**
     * @param x x in world coords
     * @param y y in world coords
     * @param z z in world coords
     * @param type the block type
     */
    public abstract void setBlock(int x, int y, int z, String type);

    /**
     * @param x x in world coords
     * @param y y in world coords
     * @param z z in world coords
     * @return true if the block is air
     */
    public abstract boolean isAir(int x, int y, int z);

    /**
     * @param shape the shape to draw
     * @param y1 the starting height above ground (inclusive)
     * @param y2 the end height above ground (exclusive)
     * @param type the block type
     */
    public void fillShapeOnTerrain(Shape shape, int y1, int y2, String type) {
        Rectangle rc = getIntersectionArea(shape.getBounds());

        if (rc.isEmpty()) {
            return;
        }
        
//        Block block = blockType.apply(type);

        for (int z = rc.y; z < rc.y + rc.height; z++) {
            for (int x = rc.x; x < rc.x + rc.width; x++) {
            
                if (shape.contains(x, z)) {
                    int terrain = heightMap.apply(x, z);
                    
                    for (int y = y1; y < y2; y++) {
                        setBlock(x, terrain + y, z, type);
                    }
                }
            }
        }        
    }

    /**
     * @param shape the shape to draw
     * @param y1 the starting height above ground (inclusive)
     * @param y2 the end height above ground (exclusive)
     * @param type the block type
     */
    public void fillRectOnTerrain(Rectangle shape, int y1, int y2, String type) {
        Rectangle rc = getIntersectionArea(shape);

        if (rc.isEmpty()) {
            return;
        }
        
//        Block block = blockType.apply(type);

        for (int z = rc.y; z < rc.y + rc.height; z++) {
            for (int x = rc.x; x < rc.x + rc.width; x++) {
            
                int terrain = heightMap.apply(x, z);
                
                for (int y = y1; y < y2; y++) {
                    setBlock(x, terrain + y, z, type);
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
     * @return the area that is drawn by this brush
     */
    protected abstract Rectangle getAffectedArea();

    /**
     * @param rect the area to fill
     * @param hmBottom the height map at the bottom (inclusive)
     * @param hmTop the height map for the top (exclusive)
     * @param type the block type
     */
    public void fill(Rectangle rect, HeightMap hmBottom, HeightMap hmTop, String type) {
        Rectangle rc = getIntersectionArea(rect);

        if (rc.isEmpty()) {
            return;
        }

//        Block block = blockType.apply(type);

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
}
