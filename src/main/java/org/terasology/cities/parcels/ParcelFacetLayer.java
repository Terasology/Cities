/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.cities.parcels;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.terasology.math.geom.Rect2i;
import org.terasology.world.viewer.layers.AbstractFacetLayer;
import org.terasology.world.viewer.layers.Renders;
import org.terasology.world.viewer.layers.ZOrder;

/**
 * Draws the generated graph on a AWT graphics instance
 */
@Renders(value = ParcelFacet.class, order = ZOrder.BIOME + 1)
public class ParcelFacetLayer extends AbstractFacetLayer {

    public ParcelFacetLayer() {
        setVisible(false);
        // use default settings
    }

    @Override
    public void render(BufferedImage img, org.terasology.world.generation.Region region) {
        ParcelFacet facet = region.getFacet(ParcelFacet.class);

        Color fillColor = new Color(64, 64, 64, 128);
        Color frameColor = new Color(64, 64, 64, 224);

        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int dx = region.getRegion().minX();
        int dy = region.getRegion().minZ();
        g.translate(-dx, -dy);

        for (Parcel parcel : facet.getParcels()) {
            g.setColor(fillColor);
            Rect2i shape = parcel.getShape();
            Rectangle rc = new Rectangle(shape.minX(), shape.minY(), shape.width(), shape.height());
            g.fill(rc);
            g.setColor(frameColor);
            g.draw(rc);
        }

        g.dispose();
    }

    @Override
    public String getWorldText(org.terasology.world.generation.Region region, int wx, int wy) {
        ParcelFacet facet = region.getFacet(ParcelFacet.class);

        for (Parcel settlement : facet.getParcels()) {
            if (settlement.getShape().contains(wx, wy)) {
                return "Parcel";
            }
        }
        return null;
    }
}
