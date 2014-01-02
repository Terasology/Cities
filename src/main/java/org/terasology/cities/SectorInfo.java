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

import org.terasology.cities.model.Sector;
import org.terasology.cities.terrain.HeightMap;
import org.terasology.cities.terrain.HeightMaps;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class SectorInfo {

    private final HeightMap heightMap;  
    private final Path2D blockedArea = new Path2D.Double();
    private CityWorldConfig config;
    private Rectangle sectorRect;
    
    public SectorInfo(Sector sector, CityWorldConfig config, HeightMap hm) {
        
        int wx = sector.getCoords().x * Sector.SIZE;
        int wz = sector.getCoords().y * Sector.SIZE;
        int scale = 4;
        
        sectorRect = new Rectangle(wx, wz, Sector.SIZE, Sector.SIZE);
        this.heightMap = HeightMaps.caching(hm, sectorRect, scale);
        this.config = config;
    }

    /**
     * Marks an area as blocked 
     * @param area the area shape to add
     */
    public void addBlockedArea(Shape area) {
        
        blockedArea.append(area, false);
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
