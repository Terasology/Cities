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

import java.util.Collections;
import java.util.List;

import org.terasology.cities.BlockType;
import org.terasology.cities.DefaultShapeType;
import org.terasology.cities.ShapeType;
import org.terasology.math.geom.BaseVector3i;

import com.google.common.collect.ImmutableList;

/**
 *
 */
public class Pillar extends ColumnDecoration {

    /**
     * @param basePos the position of the base block
     */
    public Pillar(BlockType type, BaseVector3i basePos, int height) {
        super(Collections.nCopies(height, type), createList(height), basePos);
    }

    private static List<ShapeType> createList(int height) {
        return ImmutableList.<ShapeType>builder()
            .add(DefaultShapeType.PILLAR_BASE)
            .addAll(Collections.nCopies(height - 2, DefaultShapeType.PILLAR_MIDDLE))
            .add(DefaultShapeType.PILLAR_TOP)
            .build();
    }

}
