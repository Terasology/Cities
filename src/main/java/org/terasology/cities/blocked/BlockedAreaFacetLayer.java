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

package org.terasology.cities.blocked;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.terasology.math.geom.Rect2i;
import org.terasology.world.viewer.layers.AbstractFacetLayer;
import org.terasology.world.viewer.layers.Renders;
import org.terasology.world.viewer.layers.ZOrder;

/**
 * Draws the blocked area in a given image
 */
@Renders(value = BlockedAreaFacet.class, order = ZOrder.BIOME + 1)
public class BlockedAreaFacetLayer extends AbstractFacetLayer {

    public BlockedAreaFacetLayer() {
        setVisible(false);
        // use default settings
    }

    @Override
    public void render(BufferedImage img, org.terasology.world.generation.Region region) {
        BlockedAreaFacet facet = region.getFacet(BlockedAreaFacet.class);

        Graphics2D g = img.createGraphics();
        g.setColor(Color.GRAY);
        for (BlockedArea area : facet.getAreas()) {
            Rect2i areaRc = area.getWorldRegion();
            int dx = areaRc.minX() - facet.getWorldRegion().minX();
            int dy = areaRc.minY() - facet.getWorldRegion().minY();

            g.drawRect(dx, dy, areaRc.width(), areaRc.height());
            g.drawImage(area.getImage(), dx, dy, null);
        }
        g.dispose();
    }

    @Override
    public String getWorldText(org.terasology.world.generation.Region region, int wx, int wy) {
        BlockedAreaFacet facet = region.getFacet(BlockedAreaFacet.class);

        if (facet.isBlocked(wx, wy)) {
            return "Blocked";
        } else {
            return "Open";
        }
    }
}
