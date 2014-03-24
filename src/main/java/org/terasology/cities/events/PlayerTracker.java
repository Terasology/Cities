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

package org.terasology.cities.events;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3f;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cities.WorldFacade;
import org.terasology.cities.model.NamedArea;
import org.terasology.cities.model.Sector;
import org.terasology.cities.model.Sectors;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.characters.events.OnEnterBlockEvent;
import org.terasology.logic.console.Console;
import org.terasology.logic.location.LocationComponent;
import org.terasology.network.Client;
import org.terasology.network.NetworkSystem;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.In;
import org.terasology.rendering.FontColor;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Tracks player movements with respect to {@link NamedArea}s
 * @author Martin Steiger
 */
@RegisterSystem
public class PlayerTracker extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(PlayerTracker.class);
    
    @In
    private NetworkSystem networkSystem;
    
    @In
    private Console console;
    
    private final Map<String, NamedArea> prevAreaMap = Maps.newHashMap();
    
    /**
     * Called whenever a block is entered
     * @param event the event
     * @param entity the character entity reference "player:engine"
     */
    @ReceiveEvent
    public void onEnterBlock(OnEnterBlockEvent event, EntityRef entity) {
        LocationComponent loc = entity.getComponent(LocationComponent.class);
        Vector3f worldPos3d = loc.getWorldPosition();
        Vector2d worldPos = new Vector2d(worldPos3d.x, worldPos3d.z);
        Sector sector = Sectors.getSectorForPosition(worldPos3d);
        
        Client client = networkSystem.getOwner(entity);
        
        // TODO: entity can be AI-controlled, too. These don't have an owner
        if (client == null) {
            return;
        }
        
        String id = client.getId();
        String name = client.getName();
        
        // TODO: facade is null if a different WorldGenerator is used
        WorldFacade facade = CoreRegistry.get(WorldFacade.class);
        if (facade != null) {

            NamedArea prevArea = prevAreaMap.get(id);        // can be null !
            NamedArea newArea = null;

            Set<NamedArea> areas = Sets.newHashSet();
            
            areas.addAll(facade.getCities(sector));
            areas.addAll(facade.getLakes(sector));

            for (NamedArea area : areas) {
                if (area.contains(worldPos)) {
                    if (newArea != null) {
                        logger.warn("{} appears to be in {} and {} at the same time!", name, newArea.getName(), area.getName());
                    }
                    
                    newArea = area;
                }
            }
            
            if (!Objects.equals(newArea, prevArea)) {       // both can be null
                if (newArea != null) {
                    entity.send(new OnEnterAreaEvent(newArea));
                }
                if (prevArea != null) {
                    entity.send(new OnLeaveAreaEvent(prevArea));
                }

                prevAreaMap.put(id, newArea);
            }
            
        }
    }
    
    /**
     * Called whenever a named area is entered
     * @param event the event
     * @param entity the character entity reference "player:engine"
     */
    @ReceiveEvent
    public void onEnterArea(OnEnterAreaEvent event, EntityRef entity) {
        
        Client client = networkSystem.getOwner(entity);
        String playerName = String.format("%s (%s)", client.getName(), client.getId());
        String areaName = event.getArea().getName();

        playerName = FontColor.getColored(playerName, CitiesColors.PLAYER);
        areaName = FontColor.getColored(areaName, CitiesColors.AREA);
        
        console.addMessage(playerName + " entered " + areaName);
    }
    
    /**
     * Called whenever a named area is entered
     * @param event the event
     * @param entity the character entity reference "player:engine"
     */
    @ReceiveEvent
    public void onLeaveArea(OnLeaveAreaEvent event, EntityRef entity) {
        
        Client client = networkSystem.getOwner(entity);
        String playerName = String.format("%s (%s)", client.getName(), client.getId());
        String areaName = event.getArea().getName();

        playerName = FontColor.getColored(playerName, CitiesColors.PLAYER);
        areaName = FontColor.getColored(areaName, CitiesColors.AREA);
        
        console.addMessage(playerName + " left " + areaName);
    }
}
