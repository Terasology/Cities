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

import java.util.Arrays;
import java.util.List;

import org.terasology.namegenerator.waters.WaterTheme;

/**
 * Simple theme that works without assets
 * @author Martin Steiger
 */
public class DebugWaterTheme implements WaterTheme {

    @Override
    public List<String> getNames() {
        return Arrays.asList("Kynborough", "Alison", "Roos", "Cristina", "Isata", "Mawde", "Maud", "Godlefe", "Jenefer", 
                "Sanche", "Margareta", "Katerine", "Elysabeth", "Dorathea", "Janet", "Sybyll", "Amphelice", "Margaret", 
                "Joan", "Barbery", "Elen", "Rosa", "Ibbet", "Amphillis");
    }

    @Override
    public List<String> getWaterTypes() {
        return Arrays.asList("Pond", "Tarn", "Lake", "Ocean", "Sea");
    }

}
