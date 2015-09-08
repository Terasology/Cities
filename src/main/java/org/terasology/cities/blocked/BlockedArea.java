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

package org.terasology.cities.blocked;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Rect2i;
import org.terasology.world.viewer.color.ColorModels;

/**
 * An image-based registry for blocked areas.
 */
public class BlockedArea {

    private static final Color MARKER = Color.MAGENTA;

    private final BufferedImage image;
    private final Rect2i worldRect;
    private final DataBufferInt imageBuffer;
    private final int stride; // the image scanline stride

    public BlockedArea(Rect2i targetRegion) {

        worldRect = Rect2i.createFromMinAndMax(targetRegion.min(), targetRegion.max());

        int height = targetRegion.width();
        int width = targetRegion.height();

        DirectColorModel colorModel = ColorModels.ARGB; // TODO: could be RGB
        int[] masks = colorModel.getMasks();
        imageBuffer = new DataBufferInt(width * height);
        stride = width;
        WritableRaster raster = Raster.createPackedRaster(imageBuffer, width, height, stride, masks, null);
        image = new BufferedImage(colorModel, raster, false, null);
    }

    public Rect2i getWorldRegion() {
        return worldRect;
    }

    public void addRect(Rect2i area) {
        if (worldRect.overlaps(area)) {
            Graphics2D g = image.createGraphics();
            g.translate(-worldRect.minX(), -worldRect.minY());
            g.setColor(MARKER);
            g.fillRect(area.minX(), area.minY(), area.width(), area.height());
            g.dispose();
        }
    }

    public void addLine(BaseVector2i start, BaseVector2i end, float width) {
        // TODO: check for intersection (with border offset=width) first
        Graphics2D g = image.createGraphics();
        g.setColor(MARKER);
        g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.translate(-worldRect.minX(), -worldRect.minY());
        g.drawLine(start.getX(), start.getY(), end.getX(), end.getY());
        g.dispose();
    }

    public boolean isBlocked(int worldX, int worldY) {
        int imgX = worldX - worldRect.minX();
        int imgY = worldY - worldRect.minY();
        if (imgX < 0 || imgX >= worldRect.width()) {
            throw new IllegalArgumentException("worldX " + worldX + " is illegal");
        }
        if (imgY < 0 || imgY >= worldRect.height()) {
            throw new IllegalArgumentException("worldY " + worldY + " is illegal");
        }
        return imageBuffer.getElem(imgY * stride + imgX) != 0;
    }

    public boolean isBlocked(Rect2i rect) {
        Rect2i crop = rect.intersect(worldRect);
        if (crop.isEmpty()) {
            throw new IllegalArgumentException("Invalid region " + rect);
        }

        // TODO: create Rect2i.offset()
        int minX = crop.minX() - worldRect.minX();
        int minY = crop.minY() - worldRect.minY();
        int maxX = crop.maxX() - worldRect.minX();
        int maxY = crop.maxY() - worldRect.minY();

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                if (imageBuffer.getElem(y * stride + x) != 0) {
                    return true;
                }
            }
        }

        return false;
    }


    BufferedImage getImage() {
        return image;
    }
}
