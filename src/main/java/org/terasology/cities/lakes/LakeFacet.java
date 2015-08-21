/*
 * Copyright 2015 MovingBlocks
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

package org.terasology.cities.lakes;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.terasology.cities.model.Lake;
import org.terasology.math.Region3i;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.facets.base.BaseFacet2D;
/**
 *
 */
public class LakeFacet extends BaseFacet2D {

    private Set<Lake> lakes = new LinkedHashSet<>();

    public LakeFacet(Region3i targetRegion, Border3D border) {
        super(targetRegion, border);
    }

    /**
     * @param lake
     */
    public void add(Lake lake) {
        lakes.add(lake);
    }

    /**
     * @return the lakes
     */
    public Set<Lake> getLakes() {
        return Collections.unmodifiableSet(lakes);
    }
}
