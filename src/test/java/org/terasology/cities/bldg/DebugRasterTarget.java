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

import static org.terasology.world.chunks.ChunkConstants.SIZE_X;
import static org.terasology.world.chunks.ChunkConstants.SIZE_Z;

import java.util.AbstractList;
import java.util.List;
import java.util.Set;

import org.terasology.cities.BlockTypes;
import org.terasology.cities.raster.RasterTarget;
import org.terasology.math.Region3i;
import org.terasology.math.Side;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.chunks.blockdata.TeraArray;
import org.terasology.world.chunks.blockdata.TeraDenseArray16Bit;
/**
 *
 */
public class DebugRasterTarget implements RasterTarget {

    private final Rect2i area;
    private final Region3i region;
    private final TeraArray data;

    public DebugRasterTarget(int min, int max) {
        this.data = new TeraDenseArray16Bit(SIZE_X, max - min + 1, SIZE_Z);
        this.area = Rect2i.createFromMinAndMax(0, 0, SIZE_X, SIZE_Z);
        this.region = Region3i.createFromMinMax(new Vector3i(0, min, 0), new Vector3i(SIZE_X, max, SIZE_Z));
    }

    @Override
    public void setBlock(int x, int y, int z, BlockTypes type) {
        data.set(x, y - region.minY(), z, type.ordinal());
    }

    @Override
    public void setBlock(int x, int y, int z, BlockTypes type, Set<Side> side) {
        setBlock(x, y, z, type); // ignore side flags
    }

    @Override
    public Rect2i getAffectedArea() {
        return area;
    }

    @Override
    public Region3i getAffectedRegion() {
        return region;
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
                return region.maxY() - region.minY() + 1;
            }
        };
    }
}
