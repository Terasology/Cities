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

package org.terasology.cities.model;

import java.util.Collections;
import java.util.Set;

import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Vector2i;

import com.google.common.collect.Sets;

/**
 * A site where two or more roads meet
 */
public class Junction {
    private final Set<Road> roads = Sets.newHashSet();

    private final Vector2i coords;

    /**
     * @param coords the position coordinates in sectors
     */
    public Junction(BaseVector2i coords) {
        this.coords = new Vector2i(coords);
    }

    /**
     * @return the position coordinates in sectors
     */
    public Vector2i getCoords() {
        return coords;
    }

    /**
     * @param road the road to add to this junction
     */
    public void addRoad(Road road) {
        roads.add(road);
    }

    /**
     * @return an unmodifiable view on the connected roads (can be empty, but never <code>null</code>)
     */
     public Set<Road> getRoads() {
        return Collections.unmodifiableSet(roads);
    }

    @Override
    public String toString() {
        return "Junction [" + coords + ", " + roads.size() + " roads]";
    }
}
