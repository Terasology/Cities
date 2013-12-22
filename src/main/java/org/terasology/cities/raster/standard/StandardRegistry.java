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

import org.terasology.cities.model.BattlementRoof;
import org.terasology.cities.model.City;
import org.terasology.cities.model.DomeRoof;
import org.terasology.cities.model.FlatRoof;
import org.terasology.cities.model.GateWallSegment;
import org.terasology.cities.model.HipRoof;
import org.terasology.cities.model.SaddleRoof;
import org.terasology.cities.model.SimpleFence;
import org.terasology.cities.model.SimpleHome;
import org.terasology.cities.model.SimpleLot;
import org.terasology.cities.model.SimpleTower;
import org.terasology.cities.model.SolidWallSegment;
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
        register(SimpleHome.class, new SimpleHomeRasterizer());
        register(HipRoof.class, new HipRoofRasterizer());
        register(FlatRoof.class, new FlatRoofRasterizer());
        register(SaddleRoof.class, new SaddleRoofRasterizer());
        register(DomeRoof.class, new DomeRoofRasterizer());
        register(BattlementRoof.class, new FlatRoofRasterizer());   // not sure if this is a good idea
        register(SimpleFence.class, new SimpleFenceRasterizer());
        register(SimpleTower.class, new SimpleTowerRasterizer());
        register(SolidWallSegment.class, new SolidWallSegmentRasterizer());
        register(GateWallSegment.class, new GateWallSegmentRasterizer());
    }

    /**
     * @return the instance
     */
    public static StandardRegistry getInstance() {
        return INSTANCE;
    }
}
