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

package org.terasology.cities.walls;

import java.math.RoundingMode;

import org.terasology.cities.BlockTheme;
import org.terasology.cities.BlockTypes;
import org.terasology.cities.raster.ChunkRasterTarget;
import org.terasology.cities.raster.Pen;
import org.terasology.cities.raster.Pens;
import org.terasology.cities.raster.RasterTarget;
import org.terasology.cities.raster.RasterUtil;
import org.terasology.cities.surface.InfiniteSurfaceHeightFacet;
import org.terasology.commonworld.geom.Ramp;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.LineSegment;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizer;

import com.google.common.math.DoubleMath;

/**
 * Converts a {@link TownWall} into blocks
 */
public class TownWallRasterizer implements WorldRasterizer {

    private final BlockTheme blockTheme;

    public TownWallRasterizer(BlockTheme theme) {
        this.blockTheme = theme;
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        TownWallFacet wallFacet = chunkRegion.getFacet(TownWallFacet.class);
        InfiniteSurfaceHeightFacet heightFacet = chunkRegion.getFacet(InfiniteSurfaceHeightFacet.class);
        RasterTarget target = new ChunkRasterTarget(chunk, blockTheme);
        HeightMap hm = new HeightMap() {

            @Override
            public int apply(int x, int z) {
                return TeraMath.floorToInt(heightFacet.getWorld(x, z));
            }
        };
        for (TownWall wall : wallFacet.getTownWalls()) {
            for (WallSegment seg : wall.getWalls()) {
                if (seg instanceof GateWallSegment) {
                    rasterGate(target, (GateWallSegment) seg, hm);
                }
                if (seg instanceof SolidWallSegment) {
                    rasterSolid(target, (SolidWallSegment) seg, hm);
                }
            }
        }
    }

    private void rasterSolid(RasterTarget target, SolidWallSegment element, HeightMap hm) {

        int x1 = element.getStart().getX();
        int z1 = element.getStart().getY();
        int y1 = hm.apply(x1, z1) + element.getWallHeight();
        int x2 = element.getEnd().getX();
        int z2 = element.getEnd().getY();
        int y2 = hm.apply(x2, z2) + element.getWallHeight();

        Ramp ramp = new Ramp(x1, y1, z1, x2, y2, z2);

        HeightMap topHm = new HeightMap() {

            @Override
            public int apply(int x, int z) {
                float y = ramp.getY(x, z);
                return DoubleMath.roundToInt(y, RoundingMode.HALF_UP);
            }
        };

        Pen pen = Pens.fill(target, hm, topHm, BlockTypes.TOWER_WALL);
        RasterUtil.drawLine(pen, new LineSegment(x1, z1, x2, z2));
    }

    private void rasterGate(RasterTarget target, GateWallSegment element, HeightMap terrain) {

        int x1 = element.getStart().getX();
        int z1 = element.getStart().getY();
        int y1 = terrain.apply(x1, z1) + element.getWallHeight();
        int x2 = element.getEnd().getX();
        int z2 = element.getEnd().getY();
        int y2 = terrain.apply(x2, z2) + element.getWallHeight();

        Ramp ramp = new Ramp(x1, y1, z1, x2, y2, z2);

        HeightMap hmTop = new HeightMap() {

            @Override
            public int apply(int x, int z) {
                float y = ramp.getY(x, z);
                return DoubleMath.roundToInt(y, RoundingMode.HALF_UP);
            }
        };

        HeightMap hmBottom = new HeightMap() {

            @Override
            public int apply(int x, int z) {
                int top = hmTop.apply(x, z);
                int minHeight = element.getWallHeight() / 3;

                float l = ramp.getLambda(x, z);

                // dist is max. at 0.5 * width
                float zig = Math.abs(0.5f - l);
                float arc = zig * zig * 4; // applying the sqr makes 0.25 the max. So we multiply with 4

                int base = TeraMath.floorToInt((top - minHeight) * (1 - arc));
                return base;
            }
        };

        Pen pen = Pens.fill(target, hmBottom, hmTop, BlockTypes.TOWER_WALL);
        RasterUtil.drawLine(pen, new LineSegment(x1, z1, x2, z2));
    }

    @Override
    public void initialize() {
        // nothing to do
    }

}
