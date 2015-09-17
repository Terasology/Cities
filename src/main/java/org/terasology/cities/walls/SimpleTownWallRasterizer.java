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

import org.terasology.cities.bldg.Tower;
import org.terasology.cities.raster.RasterTarget;
import org.terasology.cities.raster.RasterRegistry;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.raster.TerrainInfo;
import org.terasology.cities.raster.standard.StandardRegistry;

/**
 * TODO Type description
 */
public class SimpleTownWallRasterizer implements Rasterizer<TownWall> {

    @Override
    public void raster(RasterTarget brush, TerrainInfo ti, TownWall tw) {
        RasterRegistry registry = StandardRegistry.getInstance();

        for (WallSegment ws : tw.getWalls()) {
            registry.rasterize(brush, ti, ws);
        }

        for (Tower tower : tw.getTowers()) {
            registry.rasterize(brush, ti, tower);
        }

    }

}
