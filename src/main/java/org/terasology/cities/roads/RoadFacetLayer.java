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

package org.terasology.cities.roads;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.terasology.math.geom.BaseVector2i;
import org.terasology.world.generation.Region;
import org.terasology.world.viewer.layers.AbstractFacetLayer;
import org.terasology.world.viewer.layers.Renders;
import org.terasology.world.viewer.layers.ZOrder;

/**
 * Draws the generated graph on a AWT graphics instance
 */
@Renders(value = RoadFacet.class, order = ZOrder.BIOME + 1)
public class RoadFacetLayer extends AbstractFacetLayer {

    public RoadFacetLayer() {
        setVisible(false);
        // use default settings
    }

    @Override
    public void render(BufferedImage img, org.terasology.world.generation.Region region) {
        RoadFacet roadFacet = region.getFacet(RoadFacet.class);

        Color fillColor = new Color(128, 128, 16, 128);
        Color frameColor = new Color(128, 128, 16, 224);

        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int dx = region.getRegion().minX();
        int dy = region.getRegion().minZ();
        g.translate(-dx, -dy);


        for (Road road : roadFacet.getRoads()) {
            BaseVector2i p0 = road.getEnd0();
            BaseVector2i p1 = road.getEnd1();

            float width = road.getWidth();
            BasicStroke strokeOuter = new BasicStroke(width);
            BasicStroke strokeInner = new BasicStroke(width - 2);

            g.setColor(frameColor);
            g.setStroke(strokeOuter);
            g.drawLine(p0.getX(), p0.getY(), p1.getX(), p1.getY());
            g.setColor(fillColor);
            g.setStroke(strokeInner);
            g.drawLine(p0.getX(), p0.getY(), p1.getX(), p1.getY());
        }

        g.dispose();
    }

    @Override
    public String getWorldText(Region region, int wx, int wy) {
        return null;
    }
}
