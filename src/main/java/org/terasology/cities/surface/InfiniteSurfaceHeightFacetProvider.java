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
package org.terasology.cities.surface;

import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.commonworld.heightmap.HeightMaps;
import org.terasology.commonworld.heightmap.NoiseHeightMap;
import org.terasology.commonworld.symmetry.Symmetries;
import org.terasology.commonworld.symmetry.Symmetry;
import org.terasology.entitySystem.Component;
import org.terasology.rendering.nui.properties.OneOf.Enum;
import org.terasology.world.generation.ConfigurableFacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;

@Produces(InfiniteSurfaceHeightFacet.class)
public class InfiniteSurfaceHeightFacetProvider implements ConfigurableFacetProvider {

    private HeightMap heightMap;
    private NoiseHeightMap noiseMap;
    private InfiniteSurfaceConfiguration configuration = new InfiniteSurfaceConfiguration();

    public InfiniteSurfaceHeightFacetProvider() {
        noiseMap = new NoiseHeightMap(0);
        setConfiguration(new InfiniteSurfaceConfiguration());
    }

    @Override
    public void setSeed(long seed) {
        noiseMap.setSeed(seed);
    }

    @Override
    public void process(GeneratingRegion region) {
        InfiniteSurfaceHeightFacet facet = new InfiniteSurfaceHeightFacet() {

            @Override
            public float getWorld(int worldX, int worldY) {
                return heightMap.apply(worldX, worldY);
            }

        };

        region.setRegionFacet(InfiniteSurfaceHeightFacet.class, facet);
    }

    @Override
    public String getConfigurationName() {
        return "Height Map";
    }

    @Override
    public Component getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(Component configuration) {
        this.configuration = (InfiniteSurfaceConfiguration) configuration;
        Symmetry sym = this.configuration.symmetry.getInstance();
        if (sym != null) {
            heightMap = HeightMaps.symmetric(noiseMap, sym);
        } else {
            heightMap = noiseMap;
        }
    }

    private static enum SymmetryType {
        NONE("None", null),
        X_AXIS("X-Axis", Symmetries.alongX()),
        Z_AXIS("Z-Axis", Symmetries.alongZ()),
        POS_DIAGONAL("Positive Diagonal", Symmetries.alongPositiveDiagonal()),
        NEG_DIAGONAL("Negative Diagonal", Symmetries.alongNegativeDiagonal());

        private final String name;
        private final Symmetry instance;

        SymmetryType(String name, Symmetry instance) {
            this.name = name;
            this.instance = instance;
        }

        @Override
        public String toString() {
            return name;
        }

        public Symmetry getInstance() {
            return instance;
        }
    }

    private static class InfiniteSurfaceConfiguration implements Component {
        @Enum(label = "Symmetric World", description = "Check to create an axis-symmetric world")
        private SymmetryType symmetry = SymmetryType.NONE;
    }
}
