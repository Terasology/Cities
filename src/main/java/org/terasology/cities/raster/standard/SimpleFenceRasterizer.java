/*
 * Copyright 2013 MovingBlocks
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.terasology.cities.raster.standard;

import java.awt.Rectangle;

import org.terasology.cities.BlockTypes;
import org.terasology.cities.model.SimpleFence;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.raster.TerrainInfo;
import org.terasology.cities.terrain.HeightMap;
import org.terasology.cities.terrain.OffsetHeightMap;
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
        HeightMap hm = new OffsetHeightMap(ti.getHeightMap(), 1);
        
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
            wallX(brush, hm, wallX1, wallX2, ftop, BlockTypes.FENCE_TOP);
        }

        // bottom wall is in brush area
        if (fbot >= btop && fbot <= bbot) {
            wallX(brush, hm, wallX1, wallX2, fbot, BlockTypes.FENCE_BOTTOM);
        }

        // left wall is in brush area
        if (fleft >= bleft && fleft <= bright) {
            wallZ(brush, hm, fleft, wallZ1, wallZ2, BlockTypes.FENCE_LEFT);
        }       

        // right wall is in brush area
        if (fright >= bleft && fright <= bright) {
            wallZ(brush, hm, fright, wallZ1, wallZ2, BlockTypes.FENCE_RIGHT);
        }       

        // top-left corner post
        if (brushRc.contains(fleft, ftop)) {
            int y = hm.apply(fleft, ftop);
            
            brush.setBlock(fleft, y, ftop, BlockTypes.FENCE_NW);

            // add higher posts if necessary
            if (hm.apply(fleft + 1, ftop) > y || hm.apply(fleft, ftop + 1) > y) {
                brush.setBlock(fleft, y + 1, ftop, BlockTypes.FENCE_NW);
            }
        }

        // bottom left corner post
        if (brushRc.contains(fleft, fbot)) {
            int y = hm.apply(fleft, fbot);
            
            brush.setBlock(fleft, y, fbot, BlockTypes.FENCE_SW);

            // add higher posts if necessary
            if (hm.apply(fleft + 1, fbot) > y || hm.apply(fleft, fbot - 1) > y) {
                brush.setBlock(fleft, y + 1, fbot, BlockTypes.FENCE_SW);
            }
        }

        // bottom right corner post
        if (brushRc.contains(fright, fbot)) {
            int y = hm.apply(fright, fbot);

            brush.setBlock(fright, y, fbot, BlockTypes.FENCE_SE);

            // add higher posts if necessary
            if (hm.apply(fright - 1, fbot) > y || hm.apply(fright, fbot - 1) > y) {
                brush.setBlock(fright, y + 1, fbot, BlockTypes.FENCE_SE);
            }
        }

        // top right corner post
        if (brushRc.contains(fright, ftop)) {
            int y = hm.apply(fright, ftop);
            
            brush.setBlock(fright, y, ftop, BlockTypes.FENCE_NE);

            // add higher posts if necessary
            if (hm.apply(fright - 1, ftop) > y || hm.apply(fright, ftop + 1) > y) {
                brush.setBlock(fright, y + 1, ftop, BlockTypes.FENCE_NE);
            }
        }
        
        Vector2i gatePos = fence.getGate();
        
        if (brushRc.contains(gatePos.x, gatePos.y)) {
            String gateBlock = null;
            if (gatePos.x == fleft) { // left side
                gateBlock = BlockTypes.FENCE_GATE_LEFT;
            }
            if (gatePos.y == ftop) { // left side
                gateBlock = BlockTypes.FENCE_GATE_TOP;
            }
            if (gatePos.x == fright) { // right side
                gateBlock = BlockTypes.FENCE_GATE_RIGHT;
            }
            if (gatePos.y == fbot) { // left side
                gateBlock = BlockTypes.FENCE_GATE_BOTTOM;
            }
            
            if (gateBlock != null) {
                int y = hm.apply(gatePos.x, gatePos.y);
                brush.setBlock(gatePos.x, y, gatePos.y, gateBlock);
            }
        }
    }

    private void wallX(Brush brush, HeightMap hm, int x1, int x2, int z, String type) {
        for (int x = x1; x <= x2; x++) {
            int y = hm.apply(x, z);
            
            brush.setBlock(x, y, z, type);
            
            // if one of the neighbors is higher, add one fence block on top
            if (hm.apply(x - 1, z) > y || hm.apply(x + 1, z) > y) {
                brush.setBlock(x, y + 1, z, type);
            }
        }
    }
    
    private void wallZ(Brush brush, HeightMap hm, int x, int z1, int z2, String type) {
        for (int z = z1; z <= z2; z++) {
            int y = hm.apply(x, z);
            
            brush.setBlock(x, y, z, type);

            // if one of the neighbors is higher, add one fence block on top
            if (hm.apply(x, z - 1) > y || hm.apply(x, z + 1) > y) {
                brush.setBlock(x, y + 1, z, type);
            }
            
        }
    }

}
