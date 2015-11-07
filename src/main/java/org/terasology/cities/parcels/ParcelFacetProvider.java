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

package org.terasology.cities.parcels;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cities.blocked.BlockedAreaFacet;
import org.terasology.cities.roads.RoadFacet;
import org.terasology.cities.settlements.Settlement;
import org.terasology.cities.settlements.SettlementFacet;
import org.terasology.cities.sites.Site;
import org.terasology.cities.sites.SiteFacet;
import org.terasology.cities.terrain.BuildableTerrainFacet;
import org.terasology.commonworld.Orientation;
import org.terasology.entitySystem.Component;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Circle;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2f;
import org.terasology.rendering.nui.properties.Range;
import org.terasology.utilities.random.FastRandom;
import org.terasology.utilities.random.Random;
import org.terasology.world.generation.ConfigurableFacetProvider;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.Requires;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@Produces(ParcelFacet.class)
@Requires({
    @Facet(BlockedAreaFacet.class),
    @Facet(RoadFacet.class),                  // not really required, but roads update the blocked area facet
    @Facet(BuildableTerrainFacet.class),
    @Facet(SettlementFacet.class)
})
public class ParcelFacetProvider implements ConfigurableFacetProvider {

    private static final Logger logger = LoggerFactory.getLogger(ParcelFacetProvider.class);

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(false);

    private long seed;

    private ParcelConfiguration config = new ParcelConfiguration();

    private Cache<Site, Set<RectParcel>> cache = CacheBuilder.newBuilder().build();

    @Override
    public void setSeed(long seed) {
        this.seed = seed;
    }

    @Override
    public void process(GeneratingRegion region) {
        ParcelFacet facet = new ParcelFacet();
        SettlementFacet settlementFacet = region.getRegionFacet(SettlementFacet.class);
        BuildableTerrainFacet terrainFacet = region.getRegionFacet(BuildableTerrainFacet.class);
        BlockedAreaFacet blockedAreaFacet = region.getRegionFacet(BlockedAreaFacet.class);

        Rect2i worldRect = blockedAreaFacet.getWorldRegion();

        try {
            lock.readLock().lock();
            for (Settlement settlement : settlementFacet.getSettlements()) {
                Site site = settlement.getSite();
                if (Circle.intersects(site.getPos(), site.getRadius(), worldRect)) {
                    Set<RectParcel> parcels = cache.get(site, () -> generateParcels(settlement, blockedAreaFacet, terrainFacet));
                    for (RectParcel parcel : parcels) {
                        if (parcel.getShape().overlaps(worldRect)) {
                            facet.addParcel(site, parcel);
                        }
                    }
                }
            }
        } catch (ExecutionException e) {
            logger.warn("Could not compute parcels for '{}'", region.getRegion(), e.getCause());
        } finally {
            lock.readLock().unlock();
        }

        region.setRegionFacet(ParcelFacet.class, facet);
    }

    private Set<RectParcel> generateParcels(Settlement settlement, BlockedAreaFacet blockedAreaFacet, BuildableTerrainFacet terrainFacet) {
        Random rng = new FastRandom(seed ^ settlement.getSite().getPos().hashCode());

        Set<RectParcel> result = new LinkedHashSet<>();
        result.addAll(generateParcels(settlement, rng, 25, 40, 1, Zone.CLERICAL, blockedAreaFacet, terrainFacet));
        result.addAll(generateParcels(settlement, rng, 25, 40, 1, Zone.GOVERNMENTAL, blockedAreaFacet, terrainFacet));
        result.addAll(generateParcels(settlement, rng, config.minSize, config.maxSize, config.maxLots,
                Zone.RESIDENTIAL, blockedAreaFacet, terrainFacet));
        return result;
    }

    private Set<RectParcel> generateParcels(Settlement settlement, Random rng, float minSize, float maxSize, int count, Zone zoneType,
            BlockedAreaFacet blockedAreaFacet, BuildableTerrainFacet terrainFacet) {

        BaseVector2i center = settlement.getSite().getPos();

        Set<RectParcel> lots = new LinkedHashSet<>();  // the order is important for deterministic generation
        float maxLotRad = maxSize * (float) Math.sqrt(2) * 0.5f;
        float minRad = 5 + maxSize * 0.5f;
        float maxRad = settlement.getSettlementRadius() - maxLotRad;

        if (minRad >= maxRad) {
            return lots;        // which is empty
        }

        for (int i = 0; i < count; i++) {
            for (int j = 0; j < config.maxTries; j++) {
                float ang = rng.nextFloat(0, (float) Math.PI * 2.0f);
                float rad = rng.nextFloat(minRad, maxRad);
                float desSizeX = rng.nextFloat(minSize, maxSize);
                float desSizeZ = rng.nextFloat(minSize, maxSize);

                float x = center.getX() + rad * (float) Math.cos(ang);
                float z = center.getY() + rad * (float) Math.sin(ang);

                Vector2f pos = new Vector2f(x, z);
                Vector2f maxSpace = getMaxSpace(pos, lots);

                int sizeX = (int) Math.min(desSizeX, maxSpace.x);
                int sizeZ = (int) Math.min(desSizeZ, maxSpace.y);

                // check if enough space is available
                if (sizeX < minSize || sizeZ < minSize) {
                    continue;
                }

                int minX = TeraMath.floorToInt(pos.x() - sizeX * 0.5f);
                int minY = TeraMath.floorToInt(pos.y() - sizeZ * 0.5f);
                Rect2i shape = Rect2i.createFromMinAndSize(minX, minY, sizeX, sizeZ);

                if (terrainFacet.isBuildable(shape) && !blockedAreaFacet.isBlocked(shape)) {
                    Orientation orientation = Orientation.NORTH.getRotated(90 * rng.nextInt(4));
                    RectParcel lot = new RectParcel(shape, zoneType, orientation);
                    blockedAreaFacet.addRect(shape);
                    lots.add(lot);
                    break;
                }
            }
        }

        logger.debug("Generated {} parcels for settlement {}", lots.size(), settlement);

        return lots;
    }

    private static Vector2f getMaxSpace(Vector2f pos, Set<RectParcel> lots) {
        float maxX = Float.POSITIVE_INFINITY;
        float maxZ = Float.POSITIVE_INFINITY;

        //      xxxxxxxxxxxxxxxxxxx
        //      x                 x             (p)
        //      x        o------- x--------------|
        //      x                 x
        //      xxxxxxxxxxxxxxxxxxx       dx
        //                         <------------->

        for (RectParcel lot : lots) {
            Rect2i bounds = lot.getShape();
            float centerX = (bounds.maxX() + bounds.minX()) / 2;
            float centerY = (bounds.maxY() + bounds.minY()) / 2;
            float dx = Math.abs(pos.x() - centerX) - bounds.width() * 0.5f;
            float dz = Math.abs(pos.y() - centerY) - bounds.height() * 0.5f;

            // the point is inside -> abort
            if (dx <= 0 && dz <= 0) {
                return new Vector2f(0, 0);
            }

            // the point is diagonally outside -> restrict one of the two only
            if (dx > 0 && dz > 0) {
                // make the larger of the two smaller --> larger shape area
                if (dx > dz) {
                    maxX = Math.min(maxX, dx);
                } else {
                    maxZ = Math.min(maxZ, dz);
                }
            }

            // the z-axis is overlapping -> restrict x
            if (dx > 0 && dz <= 0) {
                maxX = Math.min(maxX, dx);
            }

            // the x-axis is overlapping -> restrict z
            if (dx <= 0 && dz > 0) {
                maxZ = Math.min(maxZ, dz);
            }
        }

        return new Vector2f(2 * maxX, 2 * maxZ);
    }

    @Override
    public String getConfigurationName() {
        return "Settlement Parcels";
    }

    @Override
    public Component getConfiguration() {
        return config;
    }

    @Override
    public void setConfiguration(Component configuration) {
        try {
            lock.writeLock().lock();
            this.config = (ParcelConfiguration) configuration;
            cache.invalidateAll();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private static class ParcelConfiguration implements Component {

        @Range(min = 5f, max = 50f, increment = 1f, precision = 0, description = "The min. parcel length")
        private float minSize = 10;

        @Range(min = 5f, max = 50f, increment = 1f, precision = 0, description = "The max. parcel length")
        private float maxSize = 18;

        @Range(min = 1, max = 5, increment = 1, precision = 0, description = "The max. number of placement attempts per parcel")
        private int maxTries = 1;

        @Range(min = 5, max = 250, increment = 1, precision = 0, description = "The max. number of parcels")
        private int maxLots = 100;
    }
}
