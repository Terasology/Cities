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

package org.terasology.cities.flora;

import java.util.List;
import java.util.Set;

import org.terasology.cities.blocked.BlockedAreaFacet;
import org.terasology.cities.roads.Road;
import org.terasology.cities.roads.RoadFacet;
import org.terasology.cities.roads.RoadSegment;
import org.terasology.core.world.generator.facetProviders.DefaultFloraProvider;
import org.terasology.core.world.generator.facets.BiomeFacet;
import org.terasology.core.world.generator.facets.FloraFacet;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.LineSegment;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.Requires;
import org.terasology.world.generation.facets.SeaLevelFacet;
import org.terasology.world.generation.facets.SurfaceHeightFacet;

import com.google.common.base.Predicate;

@Produces(FloraFacet.class)
@Requires({
        @Facet(SeaLevelFacet.class),
        @Facet(SurfaceHeightFacet.class),
        @Facet(BiomeFacet.class),
        @Facet(BlockedAreaFacet.class),
        @Facet(RoadFacet.class)
})
public class FloraFacetProvider extends DefaultFloraProvider {

    @Override
    public List<Predicate<Vector3i>> getFilters(GeneratingRegion region) {
        List<Predicate<Vector3i>> filters = super.getFilters(region);

        BlockedAreaFacet blockedAreaFacet = region.getRegionFacet(BlockedAreaFacet.class);
        filters.add(v -> !blockedAreaFacet.isBlocked(v.getX(), v.getZ()));

        RoadFacet roadFacet = region.getRegionFacet(RoadFacet.class);
        filters.add(v -> outsideRoads(v, roadFacet.getRoads()));

        return filters;
    }

    private static boolean outsideRoads(Vector3i v, Set<Road> roads) {
        int vx = v.getX();
        int vz = v.getZ();
        float minDist = 0f;   // block distance to road border
        for (Road road : roads) {
            for (RoadSegment seg : road.getSegments()) {
                BaseVector2i a = seg.getStart();
                BaseVector2i b = seg.getEnd();
                float rad = seg.getWidth() * 0.5f + minDist;
                if (LineSegment.distanceToPoint(a.getX(), a.getY(), b.getX(), b.getY(), vx, vz) < rad) {
                    return false;
                }
            }
        }
        return true;
    }
}

