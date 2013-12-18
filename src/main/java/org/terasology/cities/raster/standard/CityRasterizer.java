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

import java.awt.geom.Ellipse2D;

import javax.vecmath.Point2d;

import org.terasology.cities.model.City;
import org.terasology.cities.model.Lot;
import org.terasology.cities.model.Sector;
import org.terasology.cities.raster.Brush;
import org.terasology.cities.raster.RasterRegistry;
import org.terasology.cities.raster.Rasterizer;
import org.terasology.cities.raster.TerrainInfo;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class CityRasterizer implements Rasterizer<City> {

    @Override
    public void raster(Brush brush, TerrainInfo ti, City city) {

        Point2d pos = city.getPos();
        double rad = city.getDiameter() * 0.5;
        Ellipse2D circle = new Ellipse2D.Double(pos.x * Sector.SIZE - rad, pos.y * Sector.SIZE - rad, rad * 2, rad * 2);
        
        if (!brush.affects(circle)) {
            return;
        }

        RasterRegistry registry = StandardRegistry.getInstance();
        
        for (Lot lot : city.getLots()) {
            registry.rasterize(brush, ti, lot);
        }
    }
}
