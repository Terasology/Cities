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

import org.terasology.entitySystem.Component;
import org.terasology.rendering.nui.properties.Range;

/**
 * Configuration for the {@link CityWorldGenerator}
 * @author Martin Steiger
 */
public class CitySpawnComponent implements Component {

    @Range(min = 1, max = 5, increment = 1, precision = 1)
    private int minCitiesPerSector = 1;
    
    @Range(min = 1, max = 5, increment = 1, precision = 1)
    private int maxCitiesPerSector = 2;

    @Range(label = "Minimal town size", description = "Minimal town size in blocks", min = 10, max = 150, increment = 10, precision = 1)
    private int minRadius = 50;

    @Range(label = "Maximum town size", description = "Maximum town size in blocks", min = 100, max = 350, increment = 10, precision = 1)
    private int maxRadius = 250;
    
    @Range(label = "Minimum distance between towns", min = 100, max = 1000, increment = 10, precision = 1)
    private double maxCityDistance = 750d;
    
    /**
     * @return the minimal number of settlements per sector
     */
    public int getMinCitiesPerSector() {
        return minCitiesPerSector;
    }

    /**
     * @return the maximum number of settlements per sector
     */
    public int getMaxCitiesPerSector() {
        return maxCitiesPerSector;
    }

    /**
     * @return the minimal city radius
     */
    public int getMinCityRadius() {
        return minRadius;
    }

    /**
     * @return the maximum city radius
     */
    public int getMaxCityRadius() {
        return maxRadius;
    }

    /**
     * @return the maximum distance between two connected settlements
     */
    public double getMaxConnectedCitiesDistance() {
        return maxCityDistance;
    }    
}
