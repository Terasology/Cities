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

package org.terasology.cities;

import org.terasology.entitySystem.Component;
import org.terasology.rendering.nui.properties.Checkbox;
import org.terasology.rendering.nui.properties.Range;

/**
 * Configuration for the {@link CityWorldGenerator}
 */
public class CityTerrainComponent implements Component {

    @Range(label = "Sea Level", description = "Height of the sea level", min = 1, max = 6, increment = 1, precision = 1)
    private int seaLevel = 2;
    
    @Range(label = "Snow Line", description = "Height of the snow line", min = 20, max = 60, increment = 1, precision = 1)
    private int snowLine = 40;
    
    @Checkbox(label = "Symmetric World", description = "Check to create an axis-symmetric world")
    private boolean symmetric = true;
    
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
     * @return true if the world is symmetric
     */
    public boolean isSymmetric() {
        return symmetric;
    }
    
}
