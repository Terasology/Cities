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

import org.terasology.math.Region3i;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Rect2i;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.facets.base.BaseFacet2D;
import org.terasology.world.viewer.color.ColorModels;

/**
 * An image-based registry for blocked areas.
 */
public class BlockedAreaFacet extends BaseFacet2D {

    private final BufferedImage image;
    private final int offX;
    private final int offY;
    private final DataBufferInt imageBuffer;

    public BlockedAreaFacet(Region3i targetRegion, Border3D border) {
        super(targetRegion, border);

        offX = getWorldRegion().minX() - getRelativeRegion().minX();
        offY = getWorldRegion().minY() - getRelativeRegion().minY();

        int height = getRelativeRegion().width(); // includes border
        int width = getRelativeRegion().height();

        DirectColorModel colorModel = ColorModels.ARGB; // TODO: could be RGB
        int[] masks = colorModel.getMasks();
        imageBuffer = new DataBufferInt(width * height);
        WritableRaster raster = Raster.createPackedRaster(imageBuffer, width, height, width, masks, null);
        image = new BufferedImage(colorModel, raster, false, null);
    }

    public void addRect(Rect2i area) {
        Graphics2D g = image.createGraphics();
        g.setColor(Color.MAGENTA);
        g.fillRect(area.minX(), area.minY(), area.width(), area.height());
        g.dispose();
    }

    public void addLine(BaseVector2i start, BaseVector2i end, float width) {
        Graphics2D g = image.createGraphics();
        g.setColor(Color.MAGENTA);
        g.drawLine(0, 0, 2, 2);
        g.setStroke(new BasicStroke(width));
        g.translate(-offX, -offY);
        g.drawLine(start.getX(), start.getY(), end.getX(), end.getY());
        g.dispose();
    }

    public boolean isBlockedWorld(int worldX, int worldY) {
        int imgX = worldX - offX;
        int imgY = worldY - offY;
        int stride = image.getWidth();
        return imageBuffer.getElem(imgY * stride + imgX) > 0;
    }

    BufferedImage getImage() {
        return image;
    }
}
