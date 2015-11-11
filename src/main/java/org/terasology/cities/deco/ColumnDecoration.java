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

package org.terasology.cities.deco;

import java.util.List;

import org.terasology.cities.BlockType;
import org.terasology.cities.ShapeType;
import org.terasology.math.geom.BaseVector3i;
import org.terasology.math.geom.ImmutableVector3i;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * A single block decoration
 */
public class ColumnDecoration implements Decoration {

    private final ImmutableVector3i pos;
    private ImmutableList<BlockType> blocks;
    private ImmutableList<ShapeType> shapes;

    /**
     * @param blockTypes the decoration type
     * @param basePos the window position
     */
    public ColumnDecoration(List<BlockType> blockTypes, List<ShapeType> shapeTypes, BaseVector3i basePos) {
        Preconditions.checkArgument(blockTypes.size() == shapeTypes.size(), "");
        this.blocks = ImmutableList.copyOf(blockTypes);
        this.shapes = ImmutableList.copyOf(shapeTypes);
        this.pos = ImmutableVector3i.createOrUse(basePos);
    }

    /**
     * @return the position
     */
    public ImmutableVector3i getBasePos() {
        return this.pos;
    }

    /**
     * @return the block type to raster
     */
    public ImmutableList<BlockType> getBlockTypes() {
        return blocks;
    }

    /**
     * @return
     */
    public List<ShapeType> getShapeTypes() {
        return shapes;
    }

    /**
     * @return
     */
    public int getHeight() {
        return blocks.size(); // both lists are equally long
    }
}
