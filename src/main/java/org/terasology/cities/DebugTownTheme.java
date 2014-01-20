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

import org.terasology.cities.testing.NameList;
import org.terasology.namegenerator.town.TownTheme;

/**
 * Simple theme that works without assets
 * @author Martin Steiger
 */
public final class DebugTownTheme implements TownTheme {
    @Override
    public List<String> getPrefixes() {
        return Arrays.asList("Old", "New", "Market", "Upper", "Nether", "Little", "Lower", "Great", "Green");
                                            
    }

    @Override
    public List<String> getPostfixes() {
        return Arrays.asList("Crossing", "Cross", "Downs", "Island", "Bridge", "Barrens", "Point", 
                "Shore", "Pond", "Barrow", "Hedge", "Crags", "Cliff",
                "Coast", "Edge", "Mill", "Field", "Bush", "Forest");
    }

    @Override
    public List<String> getNames() {
        return Arrays.asList(NameList.NAMES);
    }
}