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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cities.BlockType;
import org.terasology.cities.DefaultBlockType;
import org.terasology.math.Region3i;
import org.terasology.math.Side;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector3i;

import com.google.common.base.Function;

/**
 * Converts model elements into pixels in an image.
 */
public class ImageRasterTarget implements RasterTarget {

    private static final Logger logger = LoggerFactory.getLogger(ImageRasterTarget.class);

    private final Function<BlockType, Color> blockColor;
    private final Rect2i area;

    private final BufferedImage image;
    private final short[][] heightMap;      // [x][z]
    private final BlockType[][] typeMap;   // [x][z]

    private final int wz;
    private final int wx;

    private Region3i region;

    /**
     * @param wx the world block x of the top-left corner
     * @param wz the world block z of the top-left corner
     * @param image the image to draw onto
     * @param blockColor a mapping String type -> block
     */
    public ImageRasterTarget(int wx, int wz, BufferedImage image, Function<BlockType, Color> blockColor) {
        this.blockColor = blockColor;
        this.image = image;
        this.wx = wx;
        this.wz = wz;

        int width = image.getWidth();
        int height = image.getHeight();

        this.heightMap = new short[width][height];
        this.typeMap = new BlockType[width][height];

        this.area = Rect2i.createFromMinAndSize(wx, wz, width, height);
        this.region = Region3i.createFromMinAndSize(
                new Vector3i(wx, Short.MIN_VALUE, wz),
                new Vector3i(width, Short.MAX_VALUE - Short.MIN_VALUE, height));
    }

    @Override
    public Rect2i getAffectedArea() {
        return area;
    }

    @Override
    public Region3i getAffectedRegion() {
        return region;
    }

    /**
     * @param x x in world coords
     * @param y y in world coords
     * @param z z in world coords
     * @param type the block type
     */
    @Override
    public void setBlock(int x, int y, int z, BlockType type) {
        renderBlock(x, y, z, type);
    }

    @Override
    public void setBlock(int x, int y, int z, BlockType type, Set<Side> side) {
        renderBlock(x, y, z, type);
    }


    /**
     * @param x x in world coords
     * @param y y in world coords
     * @param z z in world coords
     * @param color the actual block color
     */
    protected void renderBlock(int x, int y, int z, BlockType type) {

        int lx = x - wx;
        int lz = z - wz;

        // TODO: remove
        final boolean debugging = true;
        final boolean warnOnly = true;
        if (debugging) {
            boolean xOk = lx >= 0 && lx < image.getWidth();
            boolean yOk = y >= getMinHeight() && y < getMaxHeight();
            boolean zOk = lz >= 0 && lz < image.getHeight();

            if (warnOnly) {
                if (!xOk) {
                    logger.warn("X value of {} not in range [{}..{}]", x, wx, wx + image.getWidth() - 1);
                    return;
                }

                if (!yOk) {
                    logger.warn("Y value of {} not in range [{}..{}]", y, getMinHeight(), getMaxHeight() - 1);
                    return;
                }

                if (!zOk) {
                    logger.warn("Z value of {} not in range [{}..{}]", z, wz, wz + image.getHeight() - 1);
                    return;
                }
            }
        }

        // if air is drawn at or below terrain level, then reduce height accordingly
        // The color remains unchanged which is wrong, but this information is not available in 2D
        if (type == DefaultBlockType.AIR) {
            // reduce top height only if the top block is replaced with air
            if (heightMap[lx][lz] == y) {
                heightMap[lx][lz] = (short) (y - 1);
            }
            return;
        }

        Color color = blockColor.apply(type);

        if (color == null) {
            logger.warn("No mapping found for {}", type);
            return;
        }

        if (heightMap[lx][lz] <= y) {
            heightMap[lx][lz] = (short) y;
            typeMap[lx][lz] = type;
            float[] hsb = new float[3];
            Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
            hsb[2] = hsb[2] * (0.5f + 0.5f * TeraMath.clamp(y / 16f));
            image.setRGB(lx, lz, Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
        }
    }

    public int getHeight(int x, int z) {
        int lx = x - wx;
        int lz = z - wz;
        return heightMap[lx][lz];
    }

    public BlockType getBlockType(int x, int z) {
        int lx = x - wx;
        int lz = z - wz;
        BlockType type = typeMap[lx][lz];
        return type;
    }

}
