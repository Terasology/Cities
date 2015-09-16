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

package org.terasology.cities.bldg;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.terasology.cities.AwtConverter;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Circle;
import org.terasology.math.geom.Rect2i;
import org.terasology.world.generation.Region;
import org.terasology.world.viewer.layers.AbstractFacetLayer;
import org.terasology.world.viewer.layers.Renders;
import org.terasology.world.viewer.layers.ZOrder;

/**
 * Draws buildings area in a given image
 */
@Renders(value = BuildingFacet.class, order = ZOrder.BIOME + 3)
public class BuildingFacetLayer extends AbstractFacetLayer {

    private static final Color WALL_COLOR = Color.LIGHT_GRAY;
    private static final Color FILL_COLOR = new Color(192, 192, 192, 128);

    public BuildingFacetLayer() {
        setVisible(true);
    }

    @Override
    public void render(BufferedImage img, Region region) {
        BuildingFacet facet = region.getFacet(BuildingFacet.class);

        Graphics2D g = img.createGraphics();
        g.translate(-facet.getWorldRegion().minX(),  -facet.getWorldRegion().minY());
        for (Building bldg : facet.getBuildings()) {
            for (BuildingPart part : bldg.getParts()) {
                java.awt.Shape shape = AwtConverter.toAwt(part.getShape());
                g.setColor(FILL_COLOR);
                g.fill(shape);
                g.setColor(WALL_COLOR);
                g.draw(shape);
            }
        }
        g.dispose();
    }

    @Override
    public String getWorldText(Region region, int wx, int wy) {
        return null;
    }
}
