/*
 * Copyright 2014 MovingBlocks
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

import java.awt.geom.Ellipse2D;

import org.terasology.cities.BlockTypes;
import org.terasology.cities.heightmap.HeightMap;
import org.terasology.cities.heightmap.HeightMaps;
import org.terasology.cities.model.bldg.RoundTower;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.raster.TerrainInfo;

/**
 * Converts a {@link RoundTower} into blocks
 * @author Martin Steiger
 */
public class RoundTowerRasterizer implements Rasterizer<RoundTower> {
    
    @Override
    public void raster(Brush brush, TerrainInfo ti, final RoundTower tower) {
        final Ellipse2D area = tower.getLayout();

        if (!brush.affects(area)) {
            return;
        }
        
        final int centerX = (int) (area.getCenterX() + 0.5);
        final int centerY = (int) (area.getCenterY() + 0.5);
        final int rad = (int) (area.getWidth() * 0.5);

        // TODO: prepare floor
        
        for (int y = 0; y < tower.getWallHeight(); y++) {
            HeightMap hm = HeightMaps.constant(tower.getBaseHeight());
            brush.drawCircle(centerX, centerY, rad, hm, BlockTypes.BUILDING_WALL);
        }
    }

}
