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

package org.terasology.cities.roads;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cities.blocked.BlockedAreaFacet;
import org.terasology.cities.sites.Site;
import org.terasology.cities.sites.SiteFacet;
import org.terasology.cities.terrain.BuildableTerrainFacet;
import org.terasology.commonworld.Orientation;
import org.terasology.commonworld.UnorderedPair;
import org.terasology.core.world.generator.facets.BiomeFacet;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.ImmutableVector2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.pathfinding.GeneralPathFinder;
import org.terasology.pathfinding.GeneralPathFinder.DefaultEdge;
import org.terasology.pathfinding.GeneralPathFinder.Edge;
import org.terasology.pathfinding.GeneralPathFinder.Path;
import org.terasology.utilities.procedural.PerlinNoise;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetBorder;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.Requires;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Provides {@link Road} instances through {@link RoadFacet}.
 */
@Produces(RoadFacet.class)
@Requires({
    @Facet(value = SiteFacet.class, border = @FacetBorder(sides = 500)),
    @Facet(BuildableTerrainFacet.class),
    @Facet(BlockedAreaFacet.class)})
public class RoadFacetProvider implements FacetProvider {

    private static final Logger logger = LoggerFactory.getLogger(RoadFacetProvider.class);

    private final Cache<UnorderedPair<Site>, Optional<Road>> roadCache = CacheBuilder.newBuilder().build();

    private PerlinNoise noiseX;
    private PerlinNoise noiseY;

    /**
     * The amplitude of the noise
     */
    private final int noiseAmp = 48;

    /**
     * segments should be about N blocks long
     */
    private final int segLength = 48;

    /**
     * The smoothness of the noise. Lower values mean smoother curvature.
     */
    private final float smooth = 0.005f;


    @Override
    public void setSeed(long seed) {
        this.noiseX = new PerlinNoise(seed ^ 533231280);
        this.noiseY = new PerlinNoise(seed ^ 198218712);
    }

    @Override
    public void process(GeneratingRegion region) {
        BuildableTerrainFacet terrainFacet = region.getRegionFacet(BuildableTerrainFacet.class);
        SiteFacet siteFacet = region.getRegionFacet(SiteFacet.class);
        BlockedAreaFacet blockedAreaFacet = region.getRegionFacet(BlockedAreaFacet.class);

        Border3D border = region.getBorderForFacet(BiomeFacet.class);
        RoadFacet roadFacet = new RoadFacet(region.getRegion(), border);

        int thres = siteFacet.getCertainWorldRegion().width() / 2;

        List<Road> candidates = new ArrayList<>();

        List<Site> siteList = new ArrayList<>(siteFacet.getSettlements());
        for (int i = 0; i < siteList.size(); i++) {
            Site siteA = siteList.get(i);
            ImmutableVector2i posA = siteA.getPos();
            for (int j = i + 1; j < siteList.size(); j++) {
                Site siteB = siteList.get(j);
                ImmutableVector2i posB = siteB.getPos();

                int distX = Math.abs(posB.getX() - posA.getX());
                int distY = Math.abs(posB.getY() - posA.getY());

                if (distX < thres && distY < thres) {
                    try {
                        Optional<Road> opt = roadCache.get(new UnorderedPair<Site>(siteA, siteB),
                                () -> tryBuild(posA, posB, 8f, terrainFacet));
                        if (opt.isPresent()) {
                            candidates.add(opt.get());
                        }
                    } catch (ExecutionException e) {
                        logger.warn("Could not compute road between '{}' and '{}'", siteA, siteB);
                    }
                }
            }
        }

        candidates.sort((a, b) -> { return Float.compare(a.getLength(), b.getLength()); });

        Map<ImmutableVector2i, Collection<Edge<ImmutableVector2i>>> sourceMap = new HashMap<>();
        GeneralPathFinder<ImmutableVector2i> pathFinder = new GeneralPathFinder<>(e -> sourceMap.getOrDefault(e, Collections.emptySet()));

        for (Road road : candidates) {

            // TODO: compute if road is even relevant for this world region first

            Optional<Path<ImmutableVector2i>> optPath = pathFinder.computePath(road.getEnd0(), road.getEnd1());

            // existing connections must be at least 25% longer than the direct connection to be added
            if (!optPath.isPresent() || optPath.get().getLength() > 1.25 * road.getLength()) {
                Edge<ImmutableVector2i> e = new DefaultEdge<ImmutableVector2i>(road.getEnd0(), road.getEnd1(), road.getLength());
                sourceMap.computeIfAbsent(road.getEnd0(), a -> new ArrayList<>()).add(e);
                sourceMap.computeIfAbsent(road.getEnd1(), a -> new ArrayList<>()).add(e);
                roadFacet.addRoad(road);

                for (RoadSegment seg : road.getSegments()) {
                    blockedAreaFacet.addLine(seg.getStart(), seg.getEnd(), road.getWidth());
                }
            }
        }
        region.setRegionFacet(RoadFacet.class, roadFacet);
    }

    private Optional<Road> tryBuild(BaseVector2i posA, BaseVector2i posB, float width, BuildableTerrainFacet terrainFacet) {

        Optional<Road> opt;
        opt = tryDirect(posA, posB, width, terrainFacet);
        if (opt.isPresent()) {
            return opt;
        }
        opt = tryPathfinder(posA, posB, width, terrainFacet);
        if (opt.isPresent()) {
            return opt;
        }

        // TODO: consider creating a bridge instead if (water-passable)

        return opt;
    }

    private Optional<Road> tryDirect(BaseVector2i posA, BaseVector2i posB, float width, BuildableTerrainFacet terrainFacet) {
        double length = posA.distance(posB);
        int segCount = TeraMath.ceilToInt(length / segLength);  // ceil avoids division by zero for short distances

        List<Vector2i> segPoints = new ArrayList<>();

        segPoints.add(new Vector2i(posA));
        for (int i = 1; i < segCount; i++) {
            Vector2i pos = BaseVector2i.lerp(posA, posB, (float) i / segCount, RoundingMode.HALF_UP);

            // first and last point receive only half the noise distortion to smoothen the end points
            float applyFactor = (i == 1 || i == segCount - 1) ? 0.5f : 1f;
            pos.x += noiseX.noise(pos.x * smooth, 0, pos.y * smooth) * noiseAmp * applyFactor;
            pos.y += noiseY.noise(pos.x * smooth, 0, pos.y * smooth) * noiseAmp * applyFactor;

            if (!terrainFacet.isPassable(pos)) {
                return Optional.empty();
            }

            segPoints.add(pos);
        }
        segPoints.add(new Vector2i(posB));

        return Optional.of(new Road(segPoints, width));
    }

    private Optional<Road> tryPathfinder(BaseVector2i posA, BaseVector2i posB, float width, BuildableTerrainFacet terrainFacet) {

        Function<Vector2i, Collection<Edge<Vector2i>>> edgeFunc = new Function<Vector2i, Collection<Edge<Vector2i>>>() {

            @Override
            public Collection<Edge<Vector2i>> apply(Vector2i v) {
                if (v.distanceSquared(posB) < segLength * segLength) {
                    return Collections.singletonList(new DefaultEdge<>(v, new Vector2i(posB), v.distance(posB)));
                }
                Collection<Edge<Vector2i>> neighs = new ArrayList<>();
                for (Orientation or : Orientation.values()) {
                    Vector2i pos = new Vector2i(or.getDir()).mul(segLength).add(v);
                    Vector2i noisePos = new Vector2i(pos);
                    noisePos.x += noiseX.noise(pos.x * smooth, 0, pos.y * smooth) * noiseAmp;
                    noisePos.y += noiseY.noise(pos.x * smooth, 0, pos.y * smooth) * noiseAmp;
                    if (terrainFacet.isPassable(noisePos)) {
                        neighs.add(new DefaultEdge<Vector2i>(v, pos, v.distance(noisePos)));
                    }
                }

                return neighs;
            }
        };

        GeneralPathFinder<Vector2i> pathFinder = new GeneralPathFinder<>(edgeFunc);

        Optional<Path<Vector2i>> optPath = pathFinder.computePath(new Vector2i(posA), new Vector2i(posB));
        if (optPath.isPresent()) {
            List<Vector2i> sequence = optPath.get().getSequence();
            for (int i = 1; i < sequence.size() - 1; i++) {
                Vector2i v = sequence.get(i);
                v.x += noiseX.noise(v.x * smooth, 0, v.y * smooth) * noiseAmp;
                v.y += noiseY.noise(v.x * smooth, 0, v.y * smooth) * noiseAmp;
            }
            return Optional.of(new Road(sequence, width));
        }

        return Optional.empty();
    }

}
