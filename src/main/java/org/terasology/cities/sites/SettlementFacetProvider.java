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

package org.terasology.cities.sites;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.terasology.cities.lakes.LakeFacet;
import org.terasology.cities.model.Lake;
import org.terasology.core.world.generator.facets.BiomeFacet;
import org.terasology.entitySystem.Component;
import org.terasology.math.Region3i;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.namegenerator.town.DebugTownTheme;
import org.terasology.namegenerator.town.TownNameProvider;
import org.terasology.rendering.nui.properties.Range;
import org.terasology.utilities.procedural.Noise;
import org.terasology.utilities.procedural.WhiteNoise;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.ConfigurableFacetProvider;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetBorder;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.Requires;

/**
 *
 */
@Produces(SettlementFacet.class)
@Requires({
    @Facet(value = LakeFacet.class, border = @FacetBorder(sides = 25 * 3 + 10))})
public class SettlementFacetProvider implements ConfigurableFacetProvider {

    private Configuration config = new Configuration();

    private long seed;

    private Noise seedNoiseGen;
    private Noise sizeNoiseGen;
    private Noise priorityNoiseGen;

    @Override
    public void setSeed(long seed) {
        this.seedNoiseGen = new WhiteNoise(seed);
        this.sizeNoiseGen = new WhiteNoise(seed ^ 0x2347928);
        this.priorityNoiseGen = new WhiteNoise(seed ^ 0x9B87F3D4);
        this.seed = seed;
    }

    @Override
    public void process(GeneratingRegion region) {

        LakeFacet lakeFacet = region.getRegionFacet(LakeFacet.class);

        Border3D border = region.getBorderForFacet(BiomeFacet.class);
        border = border.extendBy(0, 0, TeraMath.ceilToInt(config.maxRadius * 3 + config.minDistance));
        Region3i coreReg = region.getRegion();
        SettlementFacet settlementFacet = new SettlementFacet(coreReg, border);
        Rect2i worldRect = settlementFacet.getWorldRegion();

        if (coreReg.minX() == 384 && coreReg.minZ() == 256) {
            System.out.println("Sas");
        }

        List<Settlement> sites = new ArrayList<>();

        for (BaseVector2i pos : worldRect.contents()) {
            // about 0.99^10 =~ 0.90 --> about 10% hit chance
            if (seedNoiseGen.noise(pos.getX(), pos.getY()) > 0.9999) {
                float size = sizeNoiseGen.noise(pos.getX(), pos.getY());
                size = config.minRadius + (size + 1) * 0.5f * (config.maxRadius - config.minRadius);

                long nameSeed = seed ^ pos.hashCode();
                // TODO: adapt NameProvider to provide name for a given seed
                TownNameProvider ng = new TownNameProvider(nameSeed, new DebugTownTheme());
                Settlement settlement = new Settlement(ng.generateName(), pos.getX(), pos.getY(), size);
                if (terrainOk(settlement, lakeFacet)) {
                    sites.add(settlement);
                }
            }
        }

        ensureMinDistance(sites, config.minDistance);

        for (Settlement site : sites) {
            settlementFacet.addSettlement(site);
        }

        region.setRegionFacet(SettlementFacet.class, settlementFacet);
    }

    private boolean terrainOk(Settlement settlement, LakeFacet lakeFacet) {
        Vector2i center = settlement.getPos();
        int wx = center.getX();
        int wy = center.getY();
        for (Lake lake : lakeFacet.getLakes()) {
            if (lake.getContour().getPolygon().contains(wx, wy)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getConfigurationName() {
        return "Settlements";
    }

    @Override
    public Component getConfiguration() {
        return config;
    }

    @Override
    public void setConfiguration(Component configuration) {
        this.config = (Configuration) configuration;
    }

    private boolean ensureMinDistance(List<Settlement> sites, double minDist) {

        sites.sort((s1, s2) -> -Float.compare(
                priorityNoiseGen.noise(s1.getPos().getX(), s1.getPos().getY()),
                priorityNoiseGen.noise(s2.getPos().getX(), s2.getPos().getY())
                ));

        Iterator<Settlement> it = sites.iterator();
        while (it.hasNext()) {
            Settlement site = it.next();

            Vector2i thisPos = site.getPos();
            float thisPrio = priorityNoiseGen.noise(thisPos.getX(), thisPos.getY());

            for (Settlement other : sites) {
                Vector2i otherPos = other.getPos();
                double distSq = thisPos.distanceSquared(otherPos);
                double thres = minDist + site.getRadius() + other.getRadius();
                if (distSq < thres * thres) {
                    float otherPrio = priorityNoiseGen.noise(otherPos.getX(), otherPos.getY());
                    if (thisPrio < otherPrio) {
                        it.remove();
                        break;
                    }
                }
            }
        }

        return true;
    }

    private static class Configuration implements Component {

        @Range(label = "Minimal town size", description = "Minimal town size in blocks", min = 1, max = 150, increment = 10, precision = 1)
        private float minRadius = 5;

        @Range(label = "Maximum town size", description = "Maximum town size in blocks", min = 10, max = 350, increment = 10, precision = 1)
        private float maxRadius = 25;

        @Range(label = "Minimum distance between towns", min = 1, max = 1000, increment = 10, precision = 1)
        private float minDistance = 10;
    }
}
