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

package org.terasology.cities.bldg;

import org.joml.Vector3i;
import org.terasology.cities.BlockType;
import org.terasology.cities.DefaultBlockType;
import org.terasology.cities.raster.RasterTarget;
import org.terasology.math.Side;
import org.terasology.math.geom.Rect2i;
import org.terasology.world.block.BlockRegion;
import org.terasology.world.block.BlockRegions;
import org.terasology.world.chunks.blockdata.TeraArray;
import org.terasology.world.chunks.blockdata.TeraDenseArray16Bit;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.terasology.world.chunks.ChunkConstants.SIZE_X;
import static org.terasology.world.chunks.ChunkConstants.SIZE_Z;

/**
 *
 */
public class DebugRasterTarget implements RasterTarget {

    private final Rect2i area;
    private final BlockRegion region;
    private final TeraArray data;
    private final List<BlockType> mapping = new ArrayList<BlockType>();

    public DebugRasterTarget(int min, int max) {
        this.data = new TeraDenseArray16Bit(SIZE_X, max - min + 1, SIZE_Z);
        this.area = Rect2i.createFromMinAndMax(0, 0, SIZE_X, SIZE_Z);
        this.region = BlockRegions.createFromMinAndMax(new Vector3i(0, min, 0), new Vector3i(SIZE_X, max, SIZE_Z));
        this.mapping.add(DefaultBlockType.AIR); // map AIR to index zero
    }

    @Override
    public void setBlock(int x, int y, int z, BlockType type) {
        int index = mapping.indexOf(type);
        if (index == -1) {
            index = mapping.size();
            mapping.add(type);
        }
        data.set(x, y - region.getMinY(), z, index);
    }

    @Override
    public void setBlock(int x, int y, int z, BlockType type, Set<Side> side) {
        setBlock(x, y, z, type); // ignore side flags
    }

    @Override
    public Rect2i getAffectedArea() {
        return area;
    }

    @Override
    public BlockRegion getAffectedRegion() {
        return region;
    }

    public List<BlockType> getColumn(int x, int z) {
        return new AbstractList<BlockType>() {

            @Override
            public BlockType get(int index) {
                int value = data.get(x, index, z);
                return mapping.get(value);
            }

            @Override
            public int size() {
                return region.getMaxY() - region.getMinY() + 1;
            }
        };
    }
}
