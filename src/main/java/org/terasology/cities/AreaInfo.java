/*
 * Copyright 2013 MovingBlocks
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

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;

import javax.vecmath.Point2i;

import org.terasology.cities.heightmap.HeightMap;

/**
 * Contains information on whether an area is blocked or not
 * @author Martin Steiger
 */
public class AreaInfo {

    private final HeightMap heightMap;  
    private final Path2D blockedArea = new Path2D.Double();
    private final CityTerrainComponent config;
    
    /**
     * @param config the world config (sea level, etc)
     * @param hm the height map to use
     */
    public AreaInfo(CityTerrainComponent config, HeightMap hm) {

        this.heightMap = hm;
        this.config = config;
    }

    /**
     * Marks an area as blocked 
     * @param shape the area shape to add
     */
    public void addBlockedArea(Shape shape) {
        
        blockedArea.append(shape, false);
    }

    /**
     * @param rc the rectangle to check
     * @return true if the rect intersects blocked area or terrain obstacles
     */
    public boolean isBlocked(Rectangle rc) {
        // TODO: check corners first 
        for (int z = rc.y; z < rc.y + rc.height; z++) {
            for (int x = rc.x; x < rc.x + rc.width; x++) {
                if (getTerrainType(x, z) != TerrainType.LAND) {
                    return true;
                }
            }
        }
        
        return blockedArea.intersects(rc);
    }
    
    /**
     * @param x the x coordinate
     * @param z the z coordinate
     * @return true if blocked, false otherwise
     */
    public boolean isBlocked(int x, int z) {
        if (getTerrainType(x, z) != TerrainType.LAND) {
            return true;
        }
        
        return blockedArea.contains(x, z);
    }

    /**
     * @param pos the coordinate
     * @return true if blocked, false otherwise
     */
    public boolean isBlocked(Point2i pos) {
        return isBlocked(pos.x, pos.y);
    }
    
    private TerrainType getTerrainType(int x, int z) {
        int y = heightMap.apply(x, z);
        
        if (y <= config.getSeaLevel()) {
            return TerrainType.WATER;
        }
        
        if (y >= config.getSnowLine()) {
            return TerrainType.SNOW;
        }
        
        return TerrainType.LAND;
    }
    
    private static enum TerrainType {
        LAND,
        WATER,
        SNOW
    }
        
}
