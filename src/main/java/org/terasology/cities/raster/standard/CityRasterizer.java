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

import java.awt.geom.Ellipse2D;

import org.terasology.cities.model.Lot;
import org.terasology.cities.model.MedievalTown;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.RasterRegistry;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.raster.TerrainInfo;
import org.terasology.cities.walls.SimpleTownWallRasterizer;

/**
 * Converts a {@link MedievalTown} into blocks
 */
public class CityRasterizer implements Rasterizer<MedievalTown> {

    @Override
    public void raster(Brush brush, TerrainInfo ti, MedievalTown city) {

        int cx = city.getPos().getX();
        int cz = city.getPos().getY();

        double rad = city.getDiameter() * 0.5;
        Ellipse2D circle = new Ellipse2D.Double(cx - rad, cz - rad, rad * 2, rad * 2);

        if (!brush.affects(circle)) {
            return;
        }

        RasterRegistry registry = StandardRegistry.getInstance();

        for (Lot lot : city.getLots()) {
            registry.rasterize(brush, ti, lot);
        }

        if (city.getTownWall().isPresent()) {
            new SimpleTownWallRasterizer().raster(brush, ti, city.getTownWall().get());
        }
    }
}
