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
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.terasology.cities.AwtConverter;
import org.terasology.cities.BlockTheme;
import org.terasology.cities.BlockTypes;
import org.terasology.cities.debug.SwingBrush;
import org.terasology.cities.raster.standard.SimpleHomeRasterizer;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.commonworld.heightmap.HeightMapAdapter;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Circle;
import org.terasology.math.geom.Rect2i;
import org.terasology.persistence.typeHandling.extensionTypes.BlockTypeHandler;
import org.terasology.rendering.nui.properties.Checkbox;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.facets.SurfaceHeightFacet;
import org.terasology.world.viewer.layers.AbstractFacetLayer;
import org.terasology.world.viewer.layers.FacetLayerConfig;
import org.terasology.world.viewer.layers.Renders;
import org.terasology.world.viewer.layers.ZOrder;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * Draws buildings area in a given image
 */
@Renders(value = BuildingFacet.class, order = ZOrder.BIOME + 3)
public class BuildingFacetLayer extends AbstractFacetLayer {

    private final BufferedImage bufferImage = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);

    private final Map<BlockTypes, Color> blockColors = ImmutableMap.<BlockTypes, Color>builder()
            .put(BlockTypes.AIR, new Color(0, 0, 0, 0))
            .put(BlockTypes.ROAD_SURFACE, new Color(160, 40, 40))
            .put(BlockTypes.LOT_EMPTY, new Color(224, 224, 64))
            .put(BlockTypes.BUILDING_WALL, new Color(158, 158, 158))
            .put(BlockTypes.BUILDING_FLOOR, new Color(100, 100, 100))
            .put(BlockTypes.BUILDING_FOUNDATION, new Color(90, 60, 60))
            .put(BlockTypes.ROOF_FLAT, new Color(255, 60, 60))
            .put(BlockTypes.ROOF_HIP, new Color(255, 60, 60))
            .put(BlockTypes.ROOF_SADDLE, new Color(224, 120, 100))
            .put(BlockTypes.ROOF_DOME, new Color(160, 190, 190))
            .put(BlockTypes.ROOF_GABLE, new Color(180, 120, 100))
            .put(BlockTypes.TOWER_WALL, new Color(200, 100, 200))
            .build();

    private enum RasterizerType {
        BASE,
        WINDOW,
        DOOR,
        ROOF
    }

    private final ListMultimap<RasterizerType, BuildingPartRasterizer<?>> rasterizers = Multimaps.newListMultimap(
            new EnumMap<>(RasterizerType.class), ArrayList::new);

    private Config config = new Config();

    public BuildingFacetLayer() {
        setVisible(true);

        BlockTheme theme = null;
        rasterizers.put(RasterizerType.BASE, new SimpleHomeRasterizer(theme));
    }

    /**
     * This can be called only through reflection since Config is private
     * @param config the layer configuration info
     */
    public BuildingFacetLayer(Config config) {
        this();
        this.config = config;
    }

    @Override
    public void render(BufferedImage img, Region region) {
        int wx = region.getRegion().minX();
        int wz = region.getRegion().minZ();

        SwingBrush brush = new SwingBrush(wx, wz, img, blockColors::get);
        render(brush, region);
    }

    private void render(SwingBrush brush, Region region) {
        if (config.showBase) {
            for (BuildingPartRasterizer<?> rasterizer : rasterizers.get(RasterizerType.BASE)) {
                rasterizer.raster(brush, region);
            }
        }
    }

    @Override
    public String getWorldText(Region region, int wx, int wy) {
        int dx = bufferImage.getWidth() / 2;
        int dy = bufferImage.getHeight() / 2;
        SwingBrush brush = new SwingBrush(wx - dx, wy - dy, bufferImage, blockColors::get);
        for (RasterizerType type : rasterizers.keys()) {
            for (BuildingPartRasterizer<?> rasterizer : rasterizers.get(type)) {
                rasterizer.raster(brush, region);
            }
        }

        int height = brush.getHeight(wx, wy);
        BlockTypes type = brush.getBlockType(wx, wy);
        return type == null ? null : type.toString() + "(" + height + ")";
    }

    @Override
    public FacetLayerConfig getConfig() {
        return config;
    }

    /**
     * Persistent data
     */
    private static class Config implements FacetLayerConfig {
        @Checkbox private boolean showBase = true;
        @Checkbox private boolean showRoofs = true;
        @Checkbox private boolean showWindows = true;
        @Checkbox private boolean showDoors = true;
    }
}
