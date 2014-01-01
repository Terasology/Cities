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

/**
 * The configuration for this module
 * @author Martin Steiger
 */
public class CityWorldConfig {

    // terrain
    private int seaLevel = 2;
    private int snowLine = 40;
    
    // settlement spawning points
    private int minCitiesPerSector = 1;
    private int maxCitiesPerSector = 2;
    private int minRadius = 50;
    private int maxRadius = 250;

    private double maxCityDistance = 750d;

    /**
     * @return the sea level
     */
    public int getSeaLevel() {
        return seaLevel;
    }
    
    /**
     * @return the snow line
     */
    public int getSnowLine() {
        return snowLine;
    }

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
