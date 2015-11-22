/*
 * Copyright 2014 MovingBlocks
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
package org.terasology.cities;

import org.terasology.core.world.CoreBiome;
import org.terasology.core.world.generator.facets.BiomeFacet;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.Requires;
import org.terasology.world.generation.facets.SeaLevelFacet;
import org.terasology.world.generation.facets.SurfaceHeightFacet;
import org.terasology.world.generation.facets.SurfaceHumidityFacet;

/**
 * Determines the biome based on temperature and humidity
 */
@Produces(BiomeFacet.class)
@Requires({
    @Facet(SeaLevelFacet.class),
    @Facet(SurfaceHeightFacet.class),
    @Facet(SurfaceHumidityFacet.class)
    })
public class SimpleBiomeProvider implements FacetProvider {

    @Override
    public void setSeed(long seed) {
    }

    @Override
    public void process(GeneratingRegion region) {
        SeaLevelFacet seaLevelFacet = region.getRegionFacet(SeaLevelFacet.class);
        SurfaceHeightFacet heightFacet = region.getRegionFacet(SurfaceHeightFacet.class);
        SurfaceHumidityFacet humidityFacet = region.getRegionFacet(SurfaceHumidityFacet.class);

        Border3D border = region.getBorderForFacet(BiomeFacet.class);
        BiomeFacet biomeFacet = new BiomeFacet(region.getRegion(), border);

        int seaLevel = seaLevelFacet.getSeaLevel();

        for (BaseVector2i pos : biomeFacet.getRelativeRegion().contents()) {
            float height = heightFacet.get(pos);

            CoreBiome biome;
            if (height <= seaLevel) {
                 biome = CoreBiome.OCEAN;
            } else if (height <= seaLevel + 2) {
                biome = CoreBiome.BEACH;
            } else if (height <= seaLevel + 30) {
                float humidity = humidityFacet.get(pos);
                biome = (humidity <= 0.66f) ? CoreBiome.PLAINS : CoreBiome.FOREST;
            } else if (height <= seaLevel + 40) {
                biome = CoreBiome.MOUNTAINS;
            } else {
                biome = CoreBiome.SNOW;
            }

            biomeFacet.set(pos, biome);
        }
        region.setRegionFacet(BiomeFacet.class, biomeFacet);
    }
}
