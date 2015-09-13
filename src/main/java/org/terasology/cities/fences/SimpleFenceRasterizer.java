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

package org.terasology.cities.fences;

import java.util.EnumSet;

import org.terasology.cities.BlockTheme;
import org.terasology.cities.BlockTypes;
import org.terasology.commonworld.Orientation;
import org.terasology.math.Region3i;
import org.terasology.math.Side;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.world.block.Block;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizer;
import org.terasology.world.generation.facets.SurfaceHeightFacet;

/**
 *
 */
public class SimpleFenceRasterizer implements WorldRasterizer {

    private BlockTheme theme;

    /**
     * @param theme
     */
    public SimpleFenceRasterizer(BlockTheme theme) {
        this.theme = theme;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        FenceFacet fenceFacet = chunkRegion.getFacet(FenceFacet.class);
        SurfaceHeightFacet heightFacet = chunkRegion.getFacet(SurfaceHeightFacet.class);
        for (SimpleFence fence : fenceFacet.getFences()) {
            raster(chunk, fence, heightFacet);
        }
    }

    private void raster(CoreChunk chunk, SimpleFence fence, SurfaceHeightFacet heightFacet) {
        Rect2i fenceRc = fence.getRect();
        Region3i brushRc = chunk.getRegion();

        int fleft = fenceRc.minX();
        int ftop = fenceRc.minY();
        int fright = fenceRc.maxX();
        int fbot = fenceRc.maxY();

        int bleft = brushRc.minX();
        int btop = brushRc.minZ();
        int bright = brushRc.maxX();
        int bbot = brushRc.maxZ();

        int wallX1 = Math.max(fleft + 1, bleft);
        int wallX2 = Math.min(fright - 1, bright);

        int wallZ1 = Math.max(ftop + 1, btop);
        int wallZ2 = Math.min(fbot - 1, bbot);

        // top wall is in brush area
        if (ftop >= btop && ftop <= bbot) {
            Block block = theme.apply(BlockTypes.FENCE, EnumSet.of(Side.LEFT, Side.RIGHT));
            wallX(chunk, heightFacet, wallX1, wallX2, ftop, block);
        }

        // bottom wall is in brush area
        if (fbot >= btop && fbot <= bbot) {
            Block block = theme.apply(BlockTypes.FENCE, EnumSet.of(Side.LEFT, Side.RIGHT));
            wallX(chunk, heightFacet, wallX1, wallX2, fbot, block);
        }

        // left wall is in brush area
        if (fleft >= bleft && fleft <= bright) {
            Block block = theme.apply(BlockTypes.FENCE, EnumSet.of(Side.FRONT, Side.BACK));
            wallZ(chunk, heightFacet, fleft, wallZ1, wallZ2, block);
        }

        // right wall is in brush area
        if (fright >= bleft && fright <= bright) {
            Block block = theme.apply(BlockTypes.FENCE, EnumSet.of(Side.FRONT, Side.BACK));
            wallZ(chunk, heightFacet, fright, wallZ1, wallZ2, block);
        }

        int y;

        // top-left corner post
        y = TeraMath.floorToInt(heightFacet.getWorld(fleft, ftop)) + 1;
        if (brushRc.encompasses(fleft, y, ftop)) {
            Block cornerPost = theme.apply(BlockTypes.FENCE, EnumSet.of(Side.BACK, Side.RIGHT));
            chunk.setBlock(fleft, y, ftop, cornerPost);

            // add higher posts if necessary
            if (y + 1 <= brushRc.maxY()) {
                if (heightFacet.getWorld(fleft + 1, ftop) >= y + 1
                 || heightFacet.getWorld(fleft, ftop + 1) >= y + 1) {
                    chunk.setBlock(fleft, y + 1, ftop, cornerPost);
                }
            }
        }

        // bottom left corner post
        y = TeraMath.floorToInt(heightFacet.getWorld(fleft, fbot)) + 1;
        if (brushRc.encompasses(fleft, y, fbot)) {
            Block cornerPost = theme.apply(BlockTypes.FENCE, EnumSet.of(Side.FRONT, Side.RIGHT));
            chunk.setBlock(fleft, y, fbot, cornerPost);

            // add higher posts if necessary
            if (y + 1 <= brushRc.maxY()) {
                if (heightFacet.getWorld(fleft + 1, fbot) >= y + 1
                 || heightFacet.getWorld(fleft, fbot - 1) >= y + 1) {
                    chunk.setBlock(fleft, y + 1, fbot, cornerPost);
                }
            }
        }

        // bottom right corner post
        y = TeraMath.floorToInt(heightFacet.getWorld(fright, fbot)) + 1;
        if (brushRc.encompasses(fright, y, fbot)) {
            Block cornerPost = theme.apply(BlockTypes.FENCE, EnumSet.of(Side.FRONT, Side.LEFT));
            chunk.setBlock(fright, y, fbot, cornerPost);

            // add higher posts if necessary
            if (y + 1 <= brushRc.maxY()) {
                if (heightFacet.getWorld(fright - 1, fbot) >= y + 1
                 || heightFacet.getWorld(fright, fbot - 1) >= y + 1) {
                    chunk.setBlock(fright, y + 1, fbot, cornerPost);
                }
            }
        }

        // top right corner post
        y = TeraMath.floorToInt(heightFacet.getWorld(fright, ftop)) + 1;
        if (brushRc.encompasses(fright, y, ftop)) {
            Block cornerPost = theme.apply(BlockTypes.FENCE, EnumSet.of(Side.BACK, Side.LEFT));
            chunk.setBlock(fright, y, ftop, cornerPost);

            // add higher posts if necessary
            if (y + 1 <= brushRc.maxY()) {
                if (heightFacet.getWorld(fright - 1, ftop) >= y + 1
                 || heightFacet.getWorld(fright, ftop + 1) >= y + 1) {
                    chunk.setBlock(fright, y + 1, ftop, cornerPost);
                }
            }
        }

        // insert gate
        Vector2i gatePos = fence.getGate();
        y = TeraMath.floorToInt(heightFacet.getWorld(gatePos.getX(), gatePos.getY())) + 1;
        if (brushRc.encompasses(gatePos.x, y, gatePos.y)) {
            Side side = getSide(fence.getGateOrientation());

            if (side != null) {
                Block gateBlock = theme.apply(BlockTypes.FENCE_GATE, EnumSet.of(side));
                chunk.setBlock(gatePos.x, y, gatePos.y, gateBlock);
            }
        }
    }

    private void post(CoreChunk chunk, SurfaceHeightFacet hm, int x, int z, Orientation o) {
        Region3i region = chunk.getRegion();
        if (x >= region.minX() && x <= region.maxX() && z >= region.minZ() && z <= region.maxZ()) {
            int y = TeraMath.floorToInt(hm.getWorld(x, z)) + 1;
            if (region.encompasses(x, y, z)) {
                Orientation a = o.getRotated(180 - 45);
                Orientation b = o.getRotated(180 + 45);
                Block cornerPost = theme.apply(BlockTypes.FENCE, EnumSet.of(getSide(a), getSide(b)));
                chunk.setBlock(x, y, z, cornerPost);

                // add higher posts if necessary
                if (y + 1 <= region.maxY()) {
                    if (hm.getWorld(x + a.getDir().getX(), z + a.getDir().getY()) >= y + 1
                     || hm.getWorld(x + b.getDir().getX(), z + b.getDir().getY()) >= y + 1) {
                        chunk.setBlock(x, y + 1, z, cornerPost);
                    }
                }
            }
        }
    }

    private static Side getSide(Orientation orientation) {
        switch (orientation) {
        case WEST:
            return Side.LEFT;
        case NORTH:
            return Side.TOP;
        case EAST:
            return Side.RIGHT;
        case SOUTH:
            return Side.BACK;
        default:
            return null;
        }
    }

    private void wallX(CoreChunk chunk, SurfaceHeightFacet hm, int x1, int x2, int z, Block block) {
        int minY = chunk.getRegion().minY();
        int maxY = chunk.getRegion().maxY();

        for (int x = x1; x <= x2; x++) {
            int y = TeraMath.floorToInt(hm.getWorld(x, z)) + 1;  // one block above surface level

            if (y >= minY && y <= maxY) {
                chunk.setBlock(x, y, z, block);

                // if one of the neighbors is at least one block higher, add one fence block on top
                if (y + 1 <= maxY && hm.getWorld(x - 1, z) >= y + 1 || hm.getWorld(x + 1, z) >= y + 1) {
                    chunk.setBlock(x, y + 1, z, block);
                }
            }
        }
    }

    private void wallZ(CoreChunk chunk, SurfaceHeightFacet hm, int x, int z1, int z2, Block block) {
        int minY = chunk.getRegion().minY();
        int maxY = chunk.getRegion().maxY();

        for (int z = z1; z <= z2; z++) {
            int y = TeraMath.floorToInt(hm.getWorld(x, z)) + 1;  // one block above surface level

            if (y >= minY && y <= maxY) {
                chunk.setBlock(x, y, z, block);

                // if one of the neighbors is at least one block higher, add one fence block on top
                if (y + 1 <= maxY && hm.getWorld(x, z - 1) >= y + 1 || hm.getWorld(x, z + 1) >= y + 1) {
                    chunk.setBlock(x, y + 1, z, block);
                }
            }
        }
    }

}

