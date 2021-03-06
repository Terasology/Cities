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

package org.terasology.cities.bldg.gen;

import org.joml.RoundingMode;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector3i;
import org.terasology.cities.DefaultBlockType;
import org.terasology.cities.bldg.Building;
import org.terasology.cities.bldg.DefaultBuilding;
import org.terasology.cities.bldg.HollowBuildingPart;
import org.terasology.cities.common.Edges;
import org.terasology.cities.deco.SingleBlockDecoration;
import org.terasology.cities.model.roof.HipRoof;
import org.terasology.cities.parcels.Parcel;
import org.terasology.commonworld.Orientation;
import org.terasology.commonworld.heightmap.HeightMap;
import org.terasology.engine.math.Side;
import org.terasology.engine.utilities.procedural.WhiteNoise;
import org.terasology.engine.world.block.BlockArea;

/**
 *
 */
public class CommercialBuildingGenerator implements BuildingGenerator {

    private long seed;

    /**
     * @param seed
     */
    public CommercialBuildingGenerator(long seed) {
        this.seed = seed;
    }

    public Building generate(Parcel parcel, HeightMap hm) {
        Orientation o = parcel.getOrientation();
        DefaultBuilding bldg = new DefaultBuilding(o);

        BlockArea baseArea = new BlockArea(parcel.getShape()).expand(-4, -4);
        BlockArea roofArea = new BlockArea(baseArea).expand(2, 2);

        int wallHeight = 8;
        int arcRadius = 4;

        Vector2ic center = new Vector2i(baseArea.center(new Vector2f()), RoundingMode.FLOOR);
        int baseHeight = hm.apply(center) + 1;
        int roofBaseHeight = baseHeight + wallHeight - 1; // 1 block overlap

        HipRoof roof = new HipRoof(roofArea, roofArea, roofBaseHeight, 0.5f, roofBaseHeight + 1);

        HollowBuildingPart hall = new HollowBuildingPart(baseArea, roof, baseHeight, wallHeight, arcRadius);
        bldg.addPart(hall);

        WhiteNoise noiseGen = new WhiteNoise(seed);

        float fillFactor = 0.3f;
        BlockArea storeArea = new BlockArea(baseArea).expand(-3, -3);

        for (Vector2ic v : storeArea) {
            if (noiseGen.noise(v.x(), v.y()) * 0.5f + 0.5f < fillFactor) {
                Vector3i pos = new Vector3i(v.x(), baseHeight, v.y());
                hall.addDecoration(new SingleBlockDecoration(DefaultBlockType.BARREL, pos, Side.FRONT));
            }
        }

        BlockArea inner = baseArea.expand(-1, -1, new BlockArea(BlockArea.INVALID));
        for (int i = 0; i < 4; i++) {
            Vector2i pos = Edges.getCorner(inner, Orientation.NORTHEAST.getRotated(i * 90));
            Vector3i pos3d = new Vector3i(pos.x(), roofBaseHeight - 2, pos.y());
            hall.addDecoration(new SingleBlockDecoration(DefaultBlockType.TORCH, pos3d, Side.FRONT));
        }
        return bldg;
    }

}
