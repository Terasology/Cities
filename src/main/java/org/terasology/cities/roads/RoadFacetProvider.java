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

package org.terasology.cities.roads;

import java.util.ArrayList;
import java.util.List;

import org.terasology.cities.sites.Settlement;
import org.terasology.cities.sites.SettlementFacet;
import org.terasology.cities.terrain.BuildableTerrainFacet;
import org.terasology.core.world.generator.facets.BiomeFacet;
import org.terasology.math.geom.Vector2i;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetBorder;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.Requires;

/**
 *
 */
@Produces(RoadFacet.class)
@Requires({
    @Facet(value = SettlementFacet.class, border = @FacetBorder(sides = 500)),
    @Facet(BuildableTerrainFacet.class)})
public class RoadFacetProvider implements FacetProvider {

    @Override
    public void process(GeneratingRegion region) {
        BuildableTerrainFacet terrainFacet = region.getRegionFacet(BuildableTerrainFacet.class);
        SettlementFacet siteFacet = region.getRegionFacet(SettlementFacet.class);

        Border3D border = region.getBorderForFacet(BiomeFacet.class);
        RoadFacet roadFacet = new RoadFacet(region.getRegion(), border);

        int thres = siteFacet.getCertainWorldRegion().width() / 2;

        List<Settlement> siteList = new ArrayList<>(siteFacet.getSettlements());
        for (int i = 0; i < siteList.size(); i++) {
            Settlement siteA = siteList.get(i);
            Vector2i posA = siteA.getPos();
            for (int j = i + 1; j < siteList.size(); j++) {
                Settlement siteB = siteList.get(j);
                Vector2i posB = siteB.getPos();

                int distX = Math.abs(posB.getX() - posA.getX());
                int distY = Math.abs(posB.getY() - posA.getY());
                if (distX < thres && distY < thres) {
                    roadFacet.addRoad(new Road(posA, posB, 6f));
                }
            }
        }

        region.setRegionFacet(RoadFacet.class, roadFacet);
    }

}
