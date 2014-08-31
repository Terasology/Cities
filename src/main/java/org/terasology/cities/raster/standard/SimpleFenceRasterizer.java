/*
 * Copyright 2013 MovingBlocks
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

package org.terasology.cities.raster.standard;

import java.awt.Rectangle;
import java.util.EnumSet;
import java.util.Set;

import org.terasology.cities.BlockTypes;
import org.terasology.cities.model.SimpleFence;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.raster.TerrainInfo;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.commonworld.heightmap.HeightMaps;
import org.terasology.math.Side;
import org.terasology.math.Vector2i;

/**
 * Converts a {@link SimpleFence} into blocks
 * @author Martin Steiger
 */
public class SimpleFenceRasterizer implements Rasterizer<SimpleFence> {

    @Override
    public void raster(Brush brush, TerrainInfo ti, SimpleFence fence) {
        Rectangle fenceRc = fence.getRect();
        Rectangle brushRc = brush.getAffectedArea();
        
        if (!fenceRc.intersects(brushRc)) {
            return;
        }
        
        // for debugging only -- add +1 manually later
        HeightMap hm = HeightMaps.offset(ti.getHeightMap(), 1);
        
        int fleft = fenceRc.x;
        int ftop = fenceRc.y;
        int fright = fleft + fenceRc.width - 1;
        int fbot = ftop + fenceRc.height - 1;

        int bleft = brushRc.x;
        int btop = brushRc.y;
        int bright = bleft + brushRc.width - 1;
        int bbot = btop + brushRc.height - 1;

        int wallX1 = Math.max(fleft + 1, bleft);
        int wallX2 = Math.min(fright - 1, bright);

        int wallZ1 = Math.max(ftop + 1, btop);
        int wallZ2 = Math.min(fbot - 1, bbot);

        // top wall is in brush area
        if (ftop >= btop && ftop <= bbot) {
            wallX(brush, hm, wallX1, wallX2, ftop, BlockTypes.FENCE, EnumSet.of(Side.LEFT, Side.RIGHT));
        }

        // bottom wall is in brush area
        if (fbot >= btop && fbot <= bbot) {
            wallX(brush, hm, wallX1, wallX2, fbot, BlockTypes.FENCE, EnumSet.of(Side.LEFT, Side.RIGHT));
        }

        // left wall is in brush area
        if (fleft >= bleft && fleft <= bright) {
            wallZ(brush, hm, fleft, wallZ1, wallZ2, BlockTypes.FENCE, EnumSet.of(Side.FRONT, Side.BACK));
        }       

        // right wall is in brush area
        if (fright >= bleft && fright <= bright) {
            wallZ(brush, hm, fright, wallZ1, wallZ2, BlockTypes.FENCE, EnumSet.of(Side.FRONT, Side.BACK));
        }       

        // top-left corner post
        if (brushRc.contains(fleft, ftop)) {
            int y = hm.apply(fleft, ftop);
            
            brush.setBlock(fleft, y, ftop, BlockTypes.FENCE, EnumSet.of(Side.BACK, Side.RIGHT));

            // add higher posts if necessary
            if (hm.apply(fleft + 1, ftop) > y || hm.apply(fleft, ftop + 1) > y) {
                brush.setBlock(fleft, y + 1, ftop, BlockTypes.FENCE, EnumSet.of(Side.BACK, Side.RIGHT));
            }
        }

        // bottom left corner post
        if (brushRc.contains(fleft, fbot)) {
            int y = hm.apply(fleft, fbot);
            
            brush.setBlock(fleft, y, fbot, BlockTypes.FENCE, EnumSet.of(Side.FRONT, Side.RIGHT));

            // add higher posts if necessary
            if (hm.apply(fleft + 1, fbot) > y || hm.apply(fleft, fbot - 1) > y) {
                brush.setBlock(fleft, y + 1, fbot, BlockTypes.FENCE, EnumSet.of(Side.FRONT, Side.RIGHT));
            }
        }

        // bottom right corner post
        if (brushRc.contains(fright, fbot)) {
            int y = hm.apply(fright, fbot);

            brush.setBlock(fright, y, fbot, BlockTypes.FENCE, EnumSet.of(Side.FRONT, Side.LEFT));

            // add higher posts if necessary
            if (hm.apply(fright - 1, fbot) > y || hm.apply(fright, fbot - 1) > y) {
                brush.setBlock(fright, y + 1, fbot, BlockTypes.FENCE, EnumSet.of(Side.FRONT, Side.LEFT));
            }
        }

        // top right corner post
        if (brushRc.contains(fright, ftop)) {
            int y = hm.apply(fright, ftop);
            
            brush.setBlock(fright, y, ftop, BlockTypes.FENCE, EnumSet.of(Side.BACK, Side.LEFT));

            // add higher posts if necessary
            if (hm.apply(fright - 1, ftop) > y || hm.apply(fright, ftop + 1) > y) {
                brush.setBlock(fright, y + 1, ftop, BlockTypes.FENCE, EnumSet.of(Side.BACK, Side.LEFT));
            }
        }
        
        Vector2i gatePos = fence.getGate();
        
        if (brushRc.contains(gatePos.x, gatePos.y)) {
            BlockTypes gateBlock = BlockTypes.FENCE_GATE;
            Side side = null;
            if (gatePos.x == fleft) { // left side
                side = Side.LEFT;
            }
            if (gatePos.y == ftop) { // left side
                side = Side.TOP;
            }
            if (gatePos.x == fright) { // right side
                side = Side.RIGHT;
            }
            if (gatePos.y == fbot) { // left side
                side = Side.BACK;
            }
            
            if (gateBlock != null) {
                int y = hm.apply(gatePos.x, gatePos.y);
                brush.setBlock(gatePos.x, y, gatePos.y, gateBlock, EnumSet.of(side));
            }
        }
    }

    private void wallX(Brush brush, HeightMap hm, int x1, int x2, int z, BlockTypes type, Set<Side> side) {
        for (int x = x1; x <= x2; x++) {
            int y = hm.apply(x, z);
            
            brush.setBlock(x, y, z, type, side);
            
            // if one of the neighbors is higher, add one fence block on top
            if (hm.apply(x - 1, z) > y || hm.apply(x + 1, z) > y) {
                brush.setBlock(x, y + 1, z, type, side);
            }
        }
    }
    
    private void wallZ(Brush brush, HeightMap hm, int x, int z1, int z2, BlockTypes type, Set<Side> side) {
        for (int z = z1; z <= z2; z++) {
            int y = hm.apply(x, z);
            
            brush.setBlock(x, y, z, type, side);

            // if one of the neighbors is higher, add one fence block on top
            if (hm.apply(x, z - 1) > y || hm.apply(x, z + 1) > y) {
                brush.setBlock(x, y + 1, z, type, side);
            }
            
        }
    }

}
