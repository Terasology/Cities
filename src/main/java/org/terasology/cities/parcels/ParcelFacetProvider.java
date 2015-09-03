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
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cities.blocked.BlockedAreaFacet;
import org.terasology.cities.generator.LotGeneratorRandom;
import org.terasology.cities.sites.Settlement;
import org.terasology.cities.sites.SettlementFacet;
import org.terasology.cities.terrain.BuildableTerrainFacet;
import org.terasology.entitySystem.Component;
import org.terasology.math.Region3i;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.rendering.nui.properties.Range;
import org.terasology.utilities.random.FastRandom;
import org.terasology.utilities.random.Random;
import org.terasology.world.generation.ConfigurableFacetProvider;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.Requires;
import org.terasology.world.generation.Updates;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@Produces(ParcelFacet.class)
@Updates(@Facet(BlockedAreaFacet.class))
@Requires({
    @Facet(BuildableTerrainFacet.class),
    @Facet(SettlementFacet.class)
})
public class ParcelFacetProvider implements ConfigurableFacetProvider {

    private static final Logger logger = LoggerFactory.getLogger(LotGeneratorRandom.class);

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(false);

    private long seed;

    private ParcelConfiguration config = new ParcelConfiguration();

    private Cache<Settlement, Set<RectParcel>> cache = CacheBuilder.newBuilder().build();

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

        Region3i world = region.getRegion();
        Rect2i worldRect = Rect2i.createFromMinAndSize(world.minX(), world.minZ(), world.sizeX(), world.sizeZ());

        for (Settlement settlement : settlementFacet.getSettlements()) {
            try {
                lock.readLock().lock();
                Set<RectParcel> parcels = cache.get(settlement, () -> generateParcels(settlement, terrainFacet));
                for (RectParcel parcel : parcels) {
                    if (parcel.getShape().overlaps(worldRect)) {
                        facet.addParcel(settlement, parcel);
                        blockedAreaFacet.addRect(parcel.getShape());
                    }
                }
            } catch (ExecutionException e) {
                logger.warn("Could not compute parcels for '{}'", settlement, e.getCause());
            } finally {
                lock.readLock().unlock();
            }
        }

        region.setRegionFacet(ParcelFacet.class, facet);
    }

    private Set<RectParcel> generateParcels(Settlement city, BuildableTerrainFacet terrainFacet) {
        Random rand = new FastRandom(Objects.hash(seed, city));

        Vector2i center = city.getPos();

        Set<RectParcel> lots = new LinkedHashSet<>();  // the order is important for deterministic generation
        float maxLotRad = config.maxSize * (float) Math.sqrt(2) * 0.5f;
        float minRad = 5 + config.maxSize * 0.5f;
        float maxRad = city.getRadius() - maxLotRad;

        if (minRad >= maxRad) {
            return lots;        // which is empty
        }

        for (int i = 0; i < config.maxTries && lots.size() < config.maxLots;  i++) {
            double ang = rand.nextDouble(0, Math.PI * 2.0);
            double rad = rand.nextDouble(minRad, maxRad);
            double desSizeX = rand.nextDouble(config.minSize, config.maxSize);
            double desSizeZ = rand.nextDouble(config.minSize, config.maxSize);

            double x = center.x + rad * Math.cos(ang);
            double z = center.y + rad * Math.sin(ang);

            Point2d pos = new Point2d(x, z);
            Vector2d maxSpace = getMaxSpace(pos, lots);

            int sizeX = (int) Math.min(desSizeX, maxSpace.x);
            int sizeZ = (int) Math.min(desSizeZ, maxSpace.y);

            // check if enough space is available
            if (sizeX < config.minSize || sizeZ < config.minSize) {
                continue;
            }

            int minX = (int) (pos.x - sizeX * 0.5);
            int minY = (int) (pos.y - sizeZ * 0.5);
            Rect2i shape = Rect2i.createFromMinAndSize(minX, minY, sizeX, sizeZ);

            if (terrainFacet.isBuildable(shape)) {
                RectParcel lot = new RectParcel(shape);
                lots.add(lot);
            }
        }

        logger.debug("Generated {} parcels for settlement {}", lots.size(), city);

        return lots;
    }

    private Vector2d getMaxSpace(Point2d pos, Set<RectParcel> lots) {
        double maxX = Double.MAX_VALUE;
        double maxZ = Double.MAX_VALUE;

        //      xxxxxxxxxxxxxxxxxxx
        //      x                 x             (p)
        //      x        o------- x--------------|
        //      x                 x
        //      xxxxxxxxxxxxxxxxxxx       dx
        //                         <------------->

        for (RectParcel lot : lots) {
            Rect2i bounds = lot.getShape();
            double centerX = (bounds.maxX() + bounds.minX()) / 2;
            double centerY = (bounds.maxY() + bounds.minY()) / 2;
            double dx = Math.abs(pos.x - centerX) - bounds.width() * 0.5;
            double dz = Math.abs(pos.y - centerY) - bounds.height() * 0.5;

            // the point is inside -> abort
            if (dx <= 0 && dz <= 0) {
                return new Vector2d(0, 0);
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

        return new Vector2d(2 * maxX, 2 * maxZ);
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

        @Range(min = 5, max = 250, increment = 1, precision = 0, description = "The max. number of placement attempts")
        private int maxTries = 100;

        @Range(min = 5, max = 250, increment = 1, precision = 0, description = "The max. number of parcels")
        private int maxLots = 100;
    }
}
