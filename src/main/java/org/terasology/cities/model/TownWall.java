/*
 * Copyright 2013 MovingBlocks
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.terasology.cities.model;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Defines a town wall consisting of {@link Tower}s 
 * and {@link WallSegment}s.
 * @author Martin Steiger
 */
public class TownWall {

    private final List<WallSegment> walls = Lists.newArrayList();
    private final Set<Tower> towers = Sets.newHashSet();

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
    public Set<Tower> getTowers() {
        return Collections.unmodifiableSet(towers);
    }
}
