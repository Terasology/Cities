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

import org.terasology.cities.model.City;
import org.terasology.cities.model.HipRoof;
import org.terasology.cities.model.SimpleBuilding;
import org.terasology.cities.model.SimpleLot;
import org.terasology.cities.raster.RasterRegistry;
import org.terasology.cities.raster.Rasterizer;

/**
 * A thread-safe singleton implementation that provides 
 * all {@link Rasterizer} implementations from this package.
 * @author Martin Steiger
 */
public final class StandardRegistry extends RasterRegistry {
    
    private static final StandardRegistry INSTANCE = new StandardRegistry();
    
    private StandardRegistry() {
        register(City.class, new CityRasterizer());
        register(SimpleLot.class, new SimpleLotRasterizer());
        register(SimpleBuilding.class, new SimpleBuildingRasterizer());
        register(HipRoof.class, new HipRoofRasterizer());
    }

    /**
     * @return the instance
     */
    public static StandardRegistry getInstance() {
        return INSTANCE;
    }
}
