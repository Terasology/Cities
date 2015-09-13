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

package org.terasology.cities.fences;

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
@Renders(value = FenceFacet.class, order = ZOrder.BIOME + 3)
public class FenceFacetLayer extends AbstractFacetLayer {

    private static final Color FENCE_COLOR = Color.YELLOW.darker();
    private static final Color GATE_COLOR = FENCE_COLOR.darker().darker();

    public FenceFacetLayer() {
        setVisible(true);
    }

    @Override
    public void render(BufferedImage img, org.terasology.world.generation.Region region) {
        FenceFacet facet = region.getFacet(FenceFacet.class);

        Graphics2D g = img.createGraphics();
        g.translate(-facet.getWorldRegion().minX(),  -facet.getWorldRegion().minY());
        for (SimpleFence fence : facet.getFences()) {
            Rect2i areaRc = fence.getRect();

            g.setColor(FENCE_COLOR);
            g.drawRect(areaRc.minX(), areaRc.minY(), areaRc.width() - 1, areaRc.height() - 1);
            g.setColor(GATE_COLOR);
            g.drawRect(fence.getGate().getX(), fence.getGate().getY(), 0, 0);
        }
        g.dispose();
    }

    @Override
    public String getWorldText(org.terasology.world.generation.Region region, int wx, int wy) {
        return null;
    }
}
