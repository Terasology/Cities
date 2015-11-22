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
import org.terasology.cities.DefaultBlockType;
import org.terasology.math.Side;
import org.terasology.math.geom.BaseVector3i;

import com.google.common.collect.ImmutableList;

public class Pillar extends ColumnDecoration {

    /**
     * @param basePos the position of the base block
     * @param height the total height of the pillar
     */
    public Pillar(BaseVector3i basePos, int height) {
        super(
            createList(height),
            Collections.nCopies(height, (Side) null),
            basePos);
    }

    private static List<BlockType> createList(int height) {
        return ImmutableList.<BlockType>builder()
            .add(DefaultBlockType.PILLAR_BASE)
            .addAll(Collections.nCopies(height - 2, DefaultBlockType.PILLAR_MIDDLE))
            .add(DefaultBlockType.PILLAR_TOP)
            .build();
    }

}
