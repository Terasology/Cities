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

import org.terasology.cities.BlockTypes;
import org.terasology.cities.model.WallSegment;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.raster.TerrainInfo;
import org.terasology.cities.terrain.HeightMap;
import org.terasology.cities.terrain.OffsetHeightMap;

/**
 * Converts a {@link WallSegment} into blocks
 * @author Martin Steiger
 */
public class SimpleWallSegmentRasterizer implements Rasterizer<WallSegment> {

    @Override
    public void raster(Brush brush, TerrainInfo ti, WallSegment element) {

        HeightMap bottomHm = ti.getHeightMap();
        HeightMap topHm = new OffsetHeightMap(bottomHm, element.getWallHeight());
        
        int x1 = element.getStart().x;
        int z1 = element.getStart().y;
        int x2 = element.getEnd().x;
        int z2 = element.getEnd().y;
        
//        brush.draw(bottomHm, topHm, x1, z1, x2, z2, BlockTypes.TOWER_WALL);
    }

}