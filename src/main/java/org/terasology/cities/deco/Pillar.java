// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.cities.deco;

import com.google.common.collect.ImmutableList;
import org.terasology.cities.BlockType;
import org.terasology.cities.DefaultBlockType;
import org.terasology.math.geom.BaseVector3i;

import java.util.Collections;
import java.util.List;

public class Pillar extends ColumnDecoration {

    /**
     * @param basePos the position of the base block
     * @param height the total height of the pillar
     */
    public Pillar(BaseVector3i basePos, int height) {
        super(
                createList(height),
                Collections.nCopies(height, null),
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
