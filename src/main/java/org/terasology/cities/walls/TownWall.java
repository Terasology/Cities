// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.cities.walls;

import com.google.common.collect.Lists;
import org.terasology.cities.bldg.Tower;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Defines a town wall consisting of {@link Tower}s and {@link WallSegment}s.
 */
public class TownWall {

    private final List<WallSegment> walls = Lists.newArrayList();
    private final List<Tower> towers = new ArrayList<>();

    /**
     * @param wallSegment the wall segment to add
     */
    public void addWall(WallSegment wallSegment) {
        walls.add(wallSegment);
    }

    /**
     * @return an unmodifiable view on the walls
     */
    public List<WallSegment> getWalls() {
        return Collections.unmodifiableList(walls);
    }

    /**
     * @param tower the tower to add
     */
    public void addTower(Tower tower) {
        towers.add(tower);
    }

    /**
     * @return an unmodifiable view on the towers
     */
    public List<Tower> getTowers() {
        return Collections.unmodifiableList(towers);
    }
}
