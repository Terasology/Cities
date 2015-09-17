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

import org.terasology.cities.BlockTheme;
import org.terasology.cities.BlockTypes;
import org.terasology.cities.raster.ChunkRasterTarget;
import org.terasology.cities.raster.Pen;
import org.terasology.cities.raster.Pens;
import org.terasology.cities.raster.RasterTarget;
import org.terasology.cities.raster.RasterUtil;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.commonworld.heightmap.HeightMaps;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.LineSegment;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizer;
import org.terasology.world.generation.facets.SurfaceHeightFacet;

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
        SurfaceHeightFacet heightFacet = chunkRegion.getFacet(SurfaceHeightFacet.class);
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
        HeightMap topHm = HeightMaps.offset(hm, element.getWallHeight());

        int x1 = element.getStart().getX();
        int z1 = element.getStart().getY();
        int x2 = element.getEnd().getX();
        int z2 = element.getEnd().getY();

        Pen pen = Pens.fill(target, hm, topHm, BlockTypes.TOWER_WALL);
        RasterUtil.drawLine(pen, new LineSegment(x1, z1, x2, z2));
    }

    private void rasterGate(RasterTarget target, GateWallSegment element, HeightMap terrain) {

        final int x1 = element.getStart().getX();
        final int z1 = element.getStart().getY();
        final int x2 = element.getEnd().getX();
        final int z2 = element.getEnd().getY();
        final double width = Math.sqrt((x2 - x1) * (x2 - x1) + (z2 - z1) * (z2 - z1));
        final int height = element.getWallHeight();

        HeightMap hmBottom = new HeightMap() {

            @Override
            public int apply(int x, int z) {
                double cx = (x1 + x2) * 0.5;
                double cz = (z1 + z2) * 0.5;
                double dx = x - cx;
                double dz = z - cz;

                // dist is at maximum 0.5 * width
                double dist = Math.sqrt((dx * dx) + (dz * dz));

                // normalize
                dist = 2 * dist / width;

                // make it a squeezed circle shape
                dist = Math.min(1.0, dist * dist * 4);

                int base = terrain.apply(x, z);
                return (int) (base + height * (1.0 - dist) - 1);
            }
        };

        HeightMap hmTop = HeightMaps.offset(terrain, height);

        Pen pen = Pens.fill(target, hmBottom, hmTop, BlockTypes.TOWER_WALL);
        RasterUtil.drawLine(pen, new LineSegment(x1, z1, x2, z2));
    }

    @Override
    public void initialize() {
        // nothing to do
    }

}
