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

package org.terasology.cities.raster.standard;

import org.terasology.cities.BlockTypes;
import org.terasology.cities.bldg.SimpleWindow;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.raster.TerrainInfo;

/**
 * Converts {@link SimpleWindow} into blocks (or air actually)
 */
public class SimpleWindowRasterizer implements Rasterizer<SimpleWindow> {

    @Override
    public void raster(Brush brush, TerrainInfo ti, SimpleWindow wnd) {
        brush.fillRect(wnd.getRect(), wnd.getBaseHeight() + 1, wnd.getTopHeight() + 1, BlockTypes.AIR);
    }

}
