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
import org.terasology.rendering.nui.properties.Checkbox;
import org.terasology.rendering.nui.properties.Range;

/**
 * Configuration for the {@link CityWorldGenerator}
 * @author Martin Steiger
 */
public class CityConfigComponent implements Component {

    @Range(min = 1, max = 5, increment = 1, precision = 1)
    private int minCitiesPerSector = 1;
    
    @Range(min = 1, max = 5, increment = 1, precision = 1)
    private int maxCitiesPerSector = 2;

    @Range(min = 10, max = 150, increment = 10, precision = 1)
    private int minRadius = 50;

    @Range(min = 100, max = 350, increment = 10, precision = 1)
    private int maxRadius = 250;
    
    @Checkbox(label = "Symmetric World", description = "Check to create an axis-symmetric world")
    private boolean symmetric = true;
}
