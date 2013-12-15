/*
 * Copyright (C) 2012-2013 Martin Steiger
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

package nexus.model.raster.standard;

import java.awt.Rectangle;

import nexus.model.raster.Brush;
import nexus.model.raster.Rasterizer;
import nexus.model.raster.Rasterizes;

import org.terasology.world.chunks.Chunk;
import org.terasology.world.generator.city.BlockTypes;
import org.terasology.world.generator.city.model.HipRoof;

/**
 * Converts a {@link HipRoof} into blocks
 * @author Martin Steiger
 */
@Rasterizes(target = HipRoof.class)
public class HipRoofRasterizer implements Rasterizer<HipRoof>
{
	@Override
	public void raster(Chunk chunk, Brush brush, HipRoof roof)
	{
        Rectangle area = roof.getArea();
        Rectangle cur = brush.getIntersectionArea(chunk, area);
        
        if (cur.isEmpty())
        	return;
        
        // this is the ground truth
        // maxHeight = baseHeight + Math.min(cur.width, cur.height) / (2 * pitch);
        
		for (int z = cur.y; z < cur.y + cur.height; z++) {
        	for (int x = cur.x; x < cur.x + cur.width; x++) {

    			int rx = x - area.x;
    			int rz = z - area.y;
    			
    			// distance to border of the roof
    			int borderDistX = Math.min(rx, area.width - 1 - rx);
    			int borderDistZ = Math.min(rz, area.height - 1 - rz);
    			
    			int dist = Math.min(borderDistX, borderDistZ);
    			
    			int y = roof.getBaseHeight() + dist / roof.getPitch();
				y = Math.min(y, roof.getMaxHeight());
    			
       			brush.setBlock(chunk, x, y, z, BlockTypes.ROOF_FLAT);
        	}
		}
		
	}

}
