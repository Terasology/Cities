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

import com.google.common.base.Optional;

/**
 * Provides information on a city
 * @author Martin Steiger
 */
public class MedievalTown extends City {

    private TownWall townWall;

    /**
     * @param diameter the city diameter in blocks
     * @param x the x coord (in blocks)
     * @param z the z coord (in blocks)
     */
    public MedievalTown(double diameter, int x, int z) {
        super(diameter, x, z);
    }

    /**
     * @return the town wall, if available
     */
    public Optional<TownWall> getTownWall() {
        return Optional.fromNullable(townWall);
    }
    
    /**
     * @param tw the town wall or <code>null</code> to clear
     */
    public void setTownWall(TownWall tw) {
        this.townWall = tw;
    }
}
