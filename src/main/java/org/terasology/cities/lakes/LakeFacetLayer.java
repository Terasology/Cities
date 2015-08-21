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

package org.terasology.cities.lakes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.terasology.cities.model.Lake;
import org.terasology.world.viewer.layers.AbstractFacetLayer;
import org.terasology.world.viewer.layers.Renders;
import org.terasology.world.viewer.layers.ZOrder;

/**
 * Draws the generated graph on a AWT graphics instance
 */
@Renders(value = LakeFacet.class, order = ZOrder.BIOME + 1)
public class LakeFacetLayer extends AbstractFacetLayer {

    public LakeFacetLayer() {
        setVisible(false);
        // use default settings
    }

    @Override
    public void render(BufferedImage img, org.terasology.world.generation.Region region) {
        LakeFacet graphFacet = region.getFacet(LakeFacet.class);

        Color fillColor = new Color(64, 64, 255, 128);
        Color frameColor = new Color(64, 64, 255, 224);

        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int dx = region.getRegion().minX();
        int dy = region.getRegion().minZ();
        g.translate(-dx, -dy);

        for (Lake lake : graphFacet.getLakes()) {
            Polygon poly = lake.getContour().getPolygon();
            g.setColor(fillColor);
            g.fill(poly);
            g.setColor(frameColor);
            g.draw(poly);
        }

        g.dispose();
    }

    @Override
    public String getWorldText(org.terasology.world.generation.Region region, int wx, int wy) {
        LakeFacet graphFacet = region.getFacet(LakeFacet.class);

        for (Lake lake : graphFacet.getLakes()) {
            if (lake.getContour().getPolygon().contains(wx, wy)) {
                return lake.getName();
            }
        }
        return null;
    }
}
