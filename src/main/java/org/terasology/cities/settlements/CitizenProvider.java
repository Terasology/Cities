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

package org.terasology.cities.settlements;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.terasology.asset.Assets;
import org.terasology.cities.surface.InfiniteSurfaceHeightFacet;
import org.terasology.entitySystem.entity.EntityStore;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.logic.behavior.BehaviorComponent;
import org.terasology.logic.behavior.asset.BehaviorTree;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.Region3i;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Circle;
import org.terasology.math.geom.ImmutableVector2i;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector3f;
import org.terasology.utilities.random.FastRandom;
import org.terasology.utilities.random.Random;
import org.terasology.world.generation.EntityBuffer;
import org.terasology.world.generation.EntityProviderPlugin;
import org.terasology.world.generation.Region;

import com.google.common.math.DoubleMath;

/**
 * Generates NPC entities inside the settlement boundaries with stray behavior.
 */
public class CitizenProvider implements EntityProviderPlugin {

    private static final float TWO_PI = (float) Math.PI * 2f;

    private final List<Prefab> fabs = new ArrayList<>();
    private BehaviorTree tree;

    @Override
    public void initialize() {
        fabs.addAll(createAssetList("Oreons:OreonBuilder", 2));  // adding it twice doubles the probability of builders
        fabs.addAll(createAssetList("Oreons:OreonGuard", 1));

        tree = Assets.get("stray", BehaviorTree.class).get();
    }

    private Collection<? extends Prefab> createAssetList(String uri, int count) {
        Optional<Prefab> opt = Assets.getPrefab(uri);
        if (opt.isPresent()) {
            return Collections.nCopies(count, opt.get());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void process(Region region, EntityBuffer buffer) {

        SettlementFacet settlementFacet = region.getFacet(SettlementFacet.class);

        Region3i reg = region.getRegion();
        Rect2i rc = Rect2i.createFromMinAndMax(reg.minX(), reg.minZ(), reg.maxX(), reg.maxZ());

        for (Settlement settlement : settlementFacet.getSettlements()) {
            ImmutableVector2i pos2d = settlement.getSite().getPos();
            float rad = settlement.getSite().getRadius();

            int x = pos2d.getX();
            int z = pos2d.getY();

            if (Circle.intersects(x, z, rad, rc)) {
                populate(region, buffer, x, z, rad);
            }
        }
    }

    private void populate(Region region, EntityBuffer buffer, int x, int z, float rad) {
        InfiniteSurfaceHeightFacet heightFacet = region.getFacet(InfiniteSurfaceHeightFacet.class);
        Random rng = new FastRandom(x ^ z);
        for (int i = 0; i < 20; i++) {
            float pr = rng.nextFloat() * rad;
            float ang = rng.nextFloat() * TWO_PI;
            int px = DoubleMath.roundToInt(x + Math.cos(ang) * pr, RoundingMode.HALF_UP);
            int pz = DoubleMath.roundToInt(z + Math.sin(ang) * pr, RoundingMode.HALF_UP);
            int py = TeraMath.floorToInt(heightFacet.getWorld(px, pz));
            if (region.getRegion().encompasses(px, py, pz)) {
                EntityStore entityStore = spawnAt(px, py, pz, rng);
                buffer.enqueue(entityStore);
            }
        }
    }

    private EntityStore spawnAt(float px, float py, int pz, Random rng) {
        Prefab npc = fabs.get(rng.nextInt(fabs.size()));

        float heightOff = 2;
        EntityStore entity = new EntityStore(npc);
        entity.addComponent(new LocationComponent(new Vector3f(px, py + heightOff, pz)));
        BehaviorComponent behavior = new BehaviorComponent();
        behavior.tree = tree;
        entity.addComponent(behavior);
        return entity;
    }

}
