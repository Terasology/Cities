/*
 * Copyright 2015 MovingBlocks
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

package org.terasology.cities;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.asset.Assets;
import org.terasology.cities.roads.Road;
import org.terasology.cities.roads.RoadFacet;
import org.terasology.cities.surface.InfiniteSurfaceHeightFacet;
import org.terasology.entitySystem.entity.EntityStore;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Vector3f;
import org.terasology.world.generation.EntityBuffer;
import org.terasology.world.generation.EntityProvider;
import org.terasology.world.generation.Region;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class SettlementEntityProvider implements EntityProvider {


    private static final Logger logger = LoggerFactory.getLogger(SettlementEntityProvider.class);

    @Override
    public void process(Region region, EntityBuffer buffer) {
        RoadFacet roadFacet = region.getFacet(RoadFacet.class);
        InfiniteSurfaceHeightFacet heightFacet = region.getFacet(InfiniteSurfaceHeightFacet.class);

        Optional<Prefab> optPrefab = Assets.getPrefab("QuestionMark");
        if (!optPrefab.isPresent()) {
            return;
        }

        Prefab prefab = optPrefab.get();
        if (!prefab.hasComponent(LocationComponent.class)) {
            return;
        }

        for (Road road : roadFacet.getRoads()) {
            for (BaseVector2i pt : road.getPoints()) {
                float y = heightFacet.getWorld(pt) + 5;
                if (region.getRegion().encompasses(pt.getX(), (int) y, pt.getY())) {
                    Vector3f position = new Vector3f(pt.getX(), y, pt.getY());
                    EntityStore builder = new EntityStore(prefab);
                    builder.addComponent(new LocationComponent(position));
                    buffer.enqueue(builder);
                }
            }
        }
   }

}
