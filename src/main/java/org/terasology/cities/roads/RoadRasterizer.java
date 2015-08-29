/*
 * Copyright 2014 MovingBlocks
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.terasology.cities.surface.InfiniteSurfaceHeightFacet;
import org.terasology.commonworld.geom.BoundingBox;
import org.terasology.commonworld.geom.Ramp;
import org.terasology.math.Region3i;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.LineSegment;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizer;

/**
 * @author Immortius
 */
public class RoadRasterizer implements WorldRasterizer {

    private Block block;

    @Override
    public void initialize() {
        BlockManager blockManager = CoreRegistry.get(BlockManager.class);
        block = blockManager.getBlock("core:Gravel");
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        RoadFacet roadFacet = chunkRegion.getFacet(RoadFacet.class);
        InfiniteSurfaceHeightFacet heightFacet = chunkRegion.getFacet(InfiniteSurfaceHeightFacet.class);

        Region3i reg = chunkRegion.getRegion();
        Rect2i rc = Rect2i.createFromMinAndMax(reg.minX(), reg.minZ(), reg.maxX(), reg.maxZ());

        Block air = CoreRegistry.get(BlockManager.class).getBlock(BlockManager.AIR_ID);

        // first compute the collection of road segments that could be relevant
        // TODO: use Line/Rectangle for each segment instead (include road width!)
        Collection<RoadSegment> segs = new ArrayList<>();
        for (Road road : roadFacet.getRoads()) {
            // TODO: check y component as well
            BoundingBox bbox = new BoundingBox();
            for (BaseVector2i pt : road.getPoints()) {
                bbox.add(pt);
            }
            float intRad = TeraMath.ceilToInt(road.getWidth() * 0.5f);
            Rect2i roadBox = bbox.toRect2i().expand(new Vector2i(intRad, intRad));

            if (roadBox.overlaps(rc)) {
                for (RoadSegment seg : road.getSegments()) {
                    segs.add(seg);
                }
            }
        }

        // compute Ramp geometry for a RoadSegment on demand
        Map<RoadSegment, Ramp> ramps = new HashMap<>();
        Function<RoadSegment, Ramp> createRamp = s -> {
            int heightA = TeraMath.floorToInt(heightFacet.getWorld(s.getStart()));
            int heightB = TeraMath.floorToInt(heightFacet.getWorld(s.getEnd()));
            return new Ramp(
                    s.getStart().getX(), s.getStart().getY(), heightA,
                    s.getEnd().getX(), s.getEnd().getY(), heightB);
        };

        for (int z = rc.minY(); z <= rc.maxY(); z++) {
            for (int x = rc.minX(); x <= rc.maxX(); x++) {
                int heightP = TeraMath.floorToInt(heightFacet.getWorld(x, z));

                for (RoadSegment seg : segs) {
                    BaseVector2i pointA = seg.getStart();
                    BaseVector2i pointB = seg.getEnd();
                    if (LineSegment.distanceToPoint(pointA.getX(), pointA.getY(),
                            pointB.getX(), pointB.getY(), x, z) < seg.getWidth()) {

                        Ramp ramp = ramps.computeIfAbsent(seg, createRamp);

                        int y = TeraMath.floorToInt(ramp.getZ(x, z));
                        if (y >= reg.minY() && y <= reg.maxY()) {
                            int cx = x - chunk.getChunkWorldOffsetX();
                            int cy = y - chunk.getChunkWorldOffsetY();
                            int cz = z - chunk.getChunkWorldOffsetZ();
                            chunk.setBlock(cx, cy, cz, block);
                        }

                        // fill up with air until default surface height is reached
                        for (int i = Math.max(reg.minY(), y + 1); i <= Math.min(reg.maxY(), heightP); i++) {
                            int cx = x - chunk.getChunkWorldOffsetX();
                            int cy = i - chunk.getChunkWorldOffsetY();
                            int cz = z - chunk.getChunkWorldOffsetZ();
                            chunk.setBlock(cx, cy, cz, air);
                        }
                    }
                }
            }
        }
    }
}
