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

package org.terasology.cities.roof;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.terasology.cities.BlockTheme;
import org.terasology.cities.BlockTypes;
import org.terasology.cities.model.roof.Roof;
import org.terasology.cities.raster.ImageRasterTarget;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.math.TeraMath;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.facets.SurfaceHeightFacet;
import org.terasology.world.viewer.layers.AbstractFacetLayer;
import org.terasology.world.viewer.layers.Renders;
import org.terasology.world.viewer.layers.ZOrder;

import com.google.common.collect.ImmutableMap;

/**
 * Draws roofs in a given image
 */
@Renders(value = RoofFacet.class, order = ZOrder.BIOME + 3)
public class RoofFacetLayer extends AbstractFacetLayer {

    private final BufferedImage bufferImage = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);

    private final Map<BlockTypes, Color> blockColors = ImmutableMap.<BlockTypes, Color>builder()
            .put(BlockTypes.AIR, new Color(0, 0, 0, 0))
            .put(BlockTypes.ROOF_FLAT, new Color(255, 60, 60))
            .put(BlockTypes.ROOF_HIP, new Color(255, 60, 60))
            .put(BlockTypes.ROOF_SADDLE, new Color(224, 120, 100))
            .put(BlockTypes.ROOF_DOME, new Color(160, 190, 190))
            .put(BlockTypes.ROOF_GABLE, new Color(180, 120, 100))
            .build();

    private Set<RoofRasterizer<?>> rasterizers = new HashSet<>();

    public RoofFacetLayer() {
        setVisible(true);

        BlockTheme theme = null;
        rasterizers.add(new FlatRoofRasterizer(theme));
        rasterizers.add(new SaddleRoofRasterizer(theme));
        rasterizers.add(new PentRoofRasterizer(theme));
        rasterizers.add(new HipRoofRasterizer(theme));
        rasterizers.add(new ConicRoofRasterizer(theme));
        rasterizers.add(new DomeRoofRasterizer(theme));
    }

    @Override
    public void render(BufferedImage img, Region region) {

        int wx = region.getRegion().minX();
        int wz = region.getRegion().minZ();
        ImageRasterTarget brush = new ImageRasterTarget(wx, wz, img, blockColors::get);
        render(brush, region);
    }

    private void render(ImageRasterTarget brush, Region chunkRegion) {
        SurfaceHeightFacet heightFacet = chunkRegion.getFacet(SurfaceHeightFacet.class);
        HeightMap hm = new HeightMap() {

            @Override
            public int apply(int x, int z) {
                return TeraMath.floorToInt(heightFacet.getWorld(x, z));
            }
        };

        RoofFacet roofFacet = chunkRegion.getFacet(RoofFacet.class);
        for (Roof roof : roofFacet.getRoofs()) {
            for (RoofRasterizer<?> rasterizer : rasterizers) {
                rasterizer.tryRaster(brush, roof, hm);
            }
        }
    }

    @Override
    public String getWorldText(Region region, int wx, int wy) {
        int dx = bufferImage.getWidth() / 2;
        int dy = bufferImage.getHeight() / 2;
        ImageRasterTarget brush = new ImageRasterTarget(wx - dx, wy - dy, bufferImage, blockColors::get);
        render(brush, region);

        int height = brush.getHeight(wx, wy);
        BlockTypes type = brush.getBlockType(wx, wy);
        return type == null ? null : type.toString() + "(" + height + ")";
    }
}
