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

package org.terasology.cities.sites;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.terasology.math.TeraMath;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.world.viewer.layers.AbstractFacetLayer;
import org.terasology.world.viewer.layers.Renders;
import org.terasology.world.viewer.layers.ZOrder;

/**
 * Draws the generated settlement sites in a AWT images.
 */
@Renders(value = SiteFacet.class, order = ZOrder.BIOME + 1)
public class SiteFacetLayer extends AbstractFacetLayer {

    private final Color fillColor = new Color(255, 64, 64, 128);
    private final Color frameColor = new Color(255, 64, 64, 224);

    public SiteFacetLayer() {
        setVisible(false);
        // use default settings
    }

    @Override
    public void render(BufferedImage img, org.terasology.world.generation.Region region) {
        SiteFacet settlementFacet = region.getFacet(SiteFacet.class);

        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int dx = region.getRegion().minX();
        int dy = region.getRegion().minZ();
        g.translate(-dx, -dy);

        for (Site settlement : settlementFacet.getSettlements()) {
            BaseVector2i center = settlement.getPos();
            int radius = TeraMath.floorToInt(settlement.getRadius());
            g.setColor(fillColor);
            g.fillOval(center.getX() - radius, center.getY() - radius, radius * 2, radius * 2);
            g.setColor(frameColor);
            g.drawOval(center.getX() - radius, center.getY() - radius, radius * 2, radius * 2);
        }

        g.dispose();
    }

    @Override
    public String getWorldText(org.terasology.world.generation.Region region, int wx, int wy) {
        SiteFacet facet = region.getFacet(SiteFacet.class);

        Vector2i cursor = new Vector2i(wx, wy);
        for (Site settlement : facet.getSettlements()) {
            if (settlement.getPos().distance(cursor) < settlement.getRadius()) {
                return settlement.toString();
            }
        }
        return null;
    }
}
