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

import java.util.AbstractList;
import java.util.List;
import java.util.Set;

import org.terasology.cities.BlockTypes;
import org.terasology.cities.raster.RasterTarget;
import org.terasology.math.Side;
import org.terasology.math.geom.Rect2i;
import org.terasology.world.chunks.ChunkConstants;
import org.terasology.world.chunks.blockdata.TeraArray;
import org.terasology.world.chunks.blockdata.TeraDenseArray16Bit;

/**
 *
 */
public class DebugRasterTarget implements RasterTarget {

    private final Rect2i area;

    private int min;
    private int max;

    private TeraArray data;

    public DebugRasterTarget(int min, int max) {
        this.min = min;
        this.max = max;
        this.data = new TeraDenseArray16Bit(ChunkConstants.SIZE_X, max - min + 1, ChunkConstants.SIZE_Z);
        this.area = Rect2i.createFromMinAndMax(0, 0, ChunkConstants.SIZE_X, ChunkConstants.SIZE_Z);
    }

    @Override
    public void setBlock(int x, int y, int z, BlockTypes type) {
        data.set(x, y - min, z, type.ordinal());
    }

    @Override
    public void setBlock(int x, int y, int z, BlockTypes type, Set<Side> side) {
        setBlock(x, y, z, type); // ignore side flags
    }

    @Override
    public int getMaxHeight() {
        return max;
    }

    @Override
    public int getMinHeight() {
        return min;
    }

    @Override
    public Rect2i getAffectedArea() {
        return area;
    }

    public List<BlockTypes> getColumn(int x, int z) {
        BlockTypes[] array = BlockTypes.values();
        return new AbstractList<BlockTypes>() {

            @Override
            public BlockTypes get(int index) {
                int value = data.get(x, index, z);
                return array[value];
            }

            @Override
            public int size() {
                return max - min + 1;
            }
        };
    }
}
