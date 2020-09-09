// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.cities.bldg;

import org.terasology.cities.BlockType;
import org.terasology.cities.DefaultBlockType;
import org.terasology.cities.raster.RasterTarget;
import org.terasology.engine.math.Region3i;
import org.terasology.engine.math.Side;
import org.terasology.engine.world.chunks.blockdata.TeraArray;
import org.terasology.engine.world.chunks.blockdata.TeraDenseArray16Bit;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector3i;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.terasology.engine.world.chunks.ChunkConstants.SIZE_X;
import static org.terasology.engine.world.chunks.ChunkConstants.SIZE_Z;

/**
 *
 */
public class DebugRasterTarget implements RasterTarget {

    private final Rect2i area;
    private final Region3i region;
    private final TeraArray data;
    private final List<BlockType> mapping = new ArrayList<BlockType>();

    public DebugRasterTarget(int min, int max) {
        this.data = new TeraDenseArray16Bit(SIZE_X, max - min + 1, SIZE_Z);
        this.area = Rect2i.createFromMinAndMax(0, 0, SIZE_X, SIZE_Z);
        this.region = Region3i.createFromMinMax(new Vector3i(0, min, 0), new Vector3i(SIZE_X, max, SIZE_Z));
        this.mapping.add(DefaultBlockType.AIR); // map AIR to index zero
    }

    @Override
    public void setBlock(int x, int y, int z, BlockType type) {
        int index = mapping.indexOf(type);
        if (index == -1) {
            index = mapping.size();
            mapping.add(type);
        }
        data.set(x, y - region.minY(), z, index);
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
    public Region3i getAffectedRegion() {
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
                return region.maxY() - region.minY() + 1;
            }
        };
    }
}
