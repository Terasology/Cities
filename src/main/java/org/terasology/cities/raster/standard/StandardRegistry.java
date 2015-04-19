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

import org.terasology.cities.model.MedievalTown;
import org.terasology.cities.model.SimpleFence;
import org.terasology.cities.model.SimpleLot;
import org.terasology.cities.model.bldg.GateWallSegment;
import org.terasology.cities.model.bldg.RoundHouse;
import org.terasology.cities.model.bldg.SimpleChurch;
import org.terasology.cities.model.bldg.SimpleHome;
import org.terasology.cities.model.bldg.SimpleTower;
import org.terasology.cities.model.bldg.SimpleWindow;
import org.terasology.cities.model.bldg.SolidWallSegment;
import org.terasology.cities.model.roof.BattlementRoof;
import org.terasology.cities.model.roof.ConicRoof;
import org.terasology.cities.model.roof.DomeRoof;
import org.terasology.cities.model.roof.FlatRoof;
import org.terasology.cities.model.roof.HipRoof;
import org.terasology.cities.model.roof.PentRoof;
import org.terasology.cities.model.roof.SaddleRoof;
import org.terasology.cities.raster.RasterRegistry;

/**
 * A thread-safe singleton implementation that provides 
 * all {@link org.terasology.cities.raster.Rasterizer} implementations from this package.
 */
public final class StandardRegistry extends RasterRegistry {
    
    private static final StandardRegistry INSTANCE = new StandardRegistry();
    
    private StandardRegistry() {
        register(MedievalTown.class, new CityRasterizer());
        register(SimpleLot.class, new SimpleLotRasterizer());
        register(SimpleHome.class, new SimpleHomeRasterizer());
        register(SimpleWindow.class, new SimpleWindowRasterizer());
        register(HipRoof.class, new HipRoofRasterizer());
        register(FlatRoof.class, new FlatRoofRasterizer());
        register(SaddleRoof.class, new SaddleRoofRasterizer());
        register(DomeRoof.class, new DomeRoofRasterizer());
        register(PentRoof.class, new PentRoofRasterizer());
        register(ConicRoof.class, new ConicRoofRasterizer());
        register(BattlementRoof.class, new FlatRoofRasterizer());   // not sure if this is a good idea
        register(SimpleFence.class, new SimpleFenceRasterizer());
        register(SimpleTower.class, new SimpleTowerRasterizer());
        register(RoundHouse.class, new RoundHouseRasterizer());
        register(SolidWallSegment.class, new SolidWallSegmentRasterizer());
        register(GateWallSegment.class, new GateWallSegmentRasterizer());
        register(SimpleChurch.class, new SimpleChurchRasterizer());
    }

    /**
     * @return the instance
     */
    public static StandardRegistry getInstance() {
        return INSTANCE;
    }
}
