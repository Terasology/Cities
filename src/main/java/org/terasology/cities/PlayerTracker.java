/*
 * Copyright 2014 MovingBlocks
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

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.vecmath.Vector3f;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cities.model.City;
import org.terasology.cities.model.Sector;
import org.terasology.cities.model.Sectors;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.ComponentSystem;
import org.terasology.entitySystem.systems.In;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.characters.events.OnEnterBlockEvent;
import org.terasology.logic.console.Console;
import org.terasology.logic.location.LocationComponent;
import org.terasology.network.Client;
import org.terasology.network.NetworkSystem;

import com.google.common.collect.Maps;

/**
 * Tracks player movements with respect to {@link City}
 * @author Martin Steiger
 */
@RegisterSystem
public class PlayerTracker implements ComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(PlayerTracker.class);
    
    @In
    private NetworkSystem networkSystem;
    
    @In
    private Console console;
    
    @In
    private WorldFacade facade;

    private final Map<String, City> cityMap = Maps.newHashMap();
    
    @Override
    public void initialise() {
        // empty
    }

    @Override
    public void shutdown() {
        // empty
    }

    /**
     * Called whenever a block is entered
     * @param event the event
     * @param entity the character entity reference "player:engine"
     */
    @ReceiveEvent
    public void onEnterBlock(OnEnterBlockEvent event, EntityRef entity) {
        LocationComponent loc = entity.getComponent(LocationComponent.class);
//        CharacterComponent charcomp = entity.getComponent(CharacterComponent.class);
//        EntityRef ccEnt = charcomp.controller;
//        ClientComponent cc = ccEnt.getComponent(ClientComponent.class);
//        DisplayInformationComponent info = cc.clientInfo.getComponent(DisplayInformationComponent.class);
        Vector3f worldPos = loc.getWorldPosition();
        Sector sector = Sectors.getSectorForPosition(worldPos);
        
        Client client = networkSystem.getOwner(entity);
        
        String id = client.getId();
        String name = client.getName();
        
        // TODO: facade is null if a different WorldGenerator is used
        if (facade != null) {
            Set<City> settlements = facade.getCities(sector);

            City prevCity = cityMap.get(id);        // can be null !
            City newCity = null;

            for (City s : settlements) {
                double cx = s.getPos().x - worldPos.x;
                double cz = s.getPos().y - worldPos.z;
                
                if (Math.sqrt(cx * cx + cz * cz) < s.getRadius()) {
                    if (newCity != null) {
                        logger.warn("{} appears to be in {} and {} at the same time!", name, newCity.getName(), s.getName());
                    }
                    
                    newCity = s;
                }
            }
            
            if (!Objects.equals(newCity, prevCity)) {       // prevCity can be null
                if (newCity == null) {
                    console.addMessage(String.format("%s left %s", name, prevCity.getName()));
                } else
                if (prevCity == null) {
                    console.addMessage(String.format("%s entered %s", name, newCity.getName()));
                } else {
                    console.addMessage(String.format("%s moved directly from %s to %s", name, prevCity.getName(), newCity.getName()));
                }
            }
            
            cityMap.put(id, newCity);
        }
    }

}
