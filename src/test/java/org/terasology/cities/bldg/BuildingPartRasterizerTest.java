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

import static org.terasology.cities.BlockTypes.AIR;
import static org.terasology.cities.BlockTypes.BUILDING_FLOOR;
import static org.terasology.cities.BlockTypes.BUILDING_FOUNDATION;
import static org.terasology.cities.BlockTypes.FENCE;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.terasology.cities.BlockTypes;
import org.terasology.cities.raster.BuildingPens;
import org.terasology.cities.raster.Pen;
import org.terasology.cities.raster.RasterTarget;
import org.terasology.cities.raster.RasterUtil;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.commonworld.heightmap.HeightMaps;
import org.terasology.math.geom.Rect2i;
/**
 * Tests the {@link BuildingPartRasterizer} class.
 */
public class BuildingPartRasterizerTest {

    private Rect2i rc = Rect2i.createFromMinAndSize(0, 0, 1, 1);

    @Test
    public void testPrepareFloorFullyInside() {
        DebugRasterTarget target = new DebugRasterTarget(0, 9);
        HeightMap terrainHeightMap = HeightMaps.constant(3);
        int baseHeight = 5;
        fillColumn(target, 0, baseHeight - 1, FENCE);
        Pen pen = BuildingPens.floorPen(target, terrainHeightMap, baseHeight, BUILDING_FLOOR);
        RasterUtil.fillRect(pen, rc);

        Assert.assertEquals(Arrays.asList(
                FENCE, FENCE, FENCE,
                BUILDING_FOUNDATION, // 3-4 are filled up
                BUILDING_FLOOR,
                AIR, AIR, AIR, AIR, AIR),
                target.getColumn(0, 0));
    }

    @Test
    public void testPrepareFloorTooLow() {
        DebugRasterTarget target = new DebugRasterTarget(-2, 2);
        HeightMap terrainHeightMap = HeightMaps.constant(3);
        int baseHeight = 5;
        fillColumn(target, -2, 2, FENCE);
        Pen pen = BuildingPens.floorPen(target, terrainHeightMap, baseHeight, BUILDING_FLOOR);
        RasterUtil.fillRect(pen, rc);

        Assert.assertEquals(Arrays.asList(
                FENCE, FENCE, FENCE, FENCE, FENCE),
                target.getColumn(0, 0));
    }

    @Test
    public void testPrepareFloorTooHigh() {
        DebugRasterTarget target = new DebugRasterTarget(6, 8);
        HeightMap terrainHeightMap = HeightMaps.constant(3);
        int baseHeight = 5;
        Pen pen = BuildingPens.floorPen(target, terrainHeightMap, baseHeight, BUILDING_FLOOR);
        RasterUtil.fillRect(pen, rc);

        Assert.assertEquals(Arrays.asList(
                AIR, AIR, AIR),
                target.getColumn(0, 0));
    }

    @Test
    public void testPrepareFloorPartlyTooHigh() {
        DebugRasterTarget target = new DebugRasterTarget(4, 6);
        HeightMap terrainHeightMap = HeightMaps.constant(3);
        int baseHeight = 6;
        Pen pen = BuildingPens.floorPen(target, terrainHeightMap, baseHeight, BUILDING_FLOOR);
        RasterUtil.fillRect(pen, rc);

        Assert.assertEquals(Arrays.asList(
                BUILDING_FOUNDATION, BUILDING_FLOOR, AIR),
                target.getColumn(0, 0));
    }

    @Test
    public void testPrepareFloorPartlyTooLow() {
        DebugRasterTarget target = new DebugRasterTarget(2, 4);
        HeightMap terrainHeightMap = HeightMaps.constant(3);
        int baseHeight = 6;
        fillColumn(target, 2, 4, FENCE);
        Pen pen = BuildingPens.floorPen(target, terrainHeightMap, baseHeight, BUILDING_FLOOR);
        RasterUtil.fillRect(pen, rc);

        Assert.assertEquals(Arrays.asList(
                FENCE, BUILDING_FOUNDATION, BUILDING_FOUNDATION),
                target.getColumn(0, 0));
    }

    @Test
    public void testPrepareFloorFillWithAir() {
        DebugRasterTarget target = new DebugRasterTarget(1, 6);
        HeightMap terrainHeightMap = HeightMaps.constant(5);
        int baseHeight = 4;
        fillColumn(target, 1, baseHeight, FENCE);
        Pen pen = BuildingPens.floorPen(target, terrainHeightMap, baseHeight, BUILDING_FLOOR);
        RasterUtil.fillRect(pen, rc);

        Assert.assertEquals(Arrays.asList(
                FENCE, FENCE, BUILDING_FLOOR, AIR, AIR, AIR),
                target.getColumn(0, 0));
    }

    @Test
    public void testPrepareFloorFillWithAirTooLow() {
        DebugRasterTarget target = new DebugRasterTarget(-2, 2);
        HeightMap terrainHeightMap = HeightMaps.constant(5);
        int baseHeight = 3;
        fillColumn(target, -2, 2, FENCE);
        Pen pen = BuildingPens.floorPen(target, terrainHeightMap, baseHeight, BUILDING_FLOOR);
        RasterUtil.fillRect(pen, rc);

        Assert.assertEquals(Arrays.asList(
                FENCE, FENCE, FENCE, FENCE, FENCE),
                target.getColumn(0, 0));
    }

    @Test
    public void testPrepareFloorFillWithAirTooHigh() {
        DebugRasterTarget target = new DebugRasterTarget(6, 10);
        HeightMap terrainHeightMap = HeightMaps.constant(5);
        int baseHeight = 3;
        Pen pen = BuildingPens.floorPen(target, terrainHeightMap, baseHeight, BUILDING_FLOOR);
        RasterUtil.fillRect(pen, rc);

        Assert.assertEquals(Arrays.asList(
                AIR, AIR, AIR, AIR, AIR),
                target.getColumn(0, 0));
    }

    private void fillColumn(RasterTarget target, int min, int max, BlockTypes type) {
        for (int y = min; y <= max; y++) {
            target.setBlock(0, y, 0, type);
        }
    }

}
