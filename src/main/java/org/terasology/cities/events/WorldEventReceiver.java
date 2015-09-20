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
package org.terasology.cities.events;

import org.terasology.cities.CityWorldGenerator;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.registry.In;
import org.terasology.world.WorldComponent;
import org.terasology.world.chunks.event.PurgeWorldEvent;
import org.terasology.world.generator.WorldGenerator;

/**
 * Receives events for {@link WorldComponent} and delegates
 * the "purgeWorld" command to the {@link CityWorldGenerator} class.
 */
@RegisterSystem
public class WorldEventReceiver extends BaseComponentSystem {

    @In
    private WorldGenerator worldGenerator;

    /**
     * @param event the event
     * @param worldEntity the world entity (empty)
     */
    @ReceiveEvent(components = {WorldComponent.class})
    public void onPurgeWorld(PurgeWorldEvent event, EntityRef worldEntity) {
        if (worldGenerator instanceof CityWorldGenerator) {
            ((CityWorldGenerator) worldGenerator).expungeCaches();
        }
    }
}
