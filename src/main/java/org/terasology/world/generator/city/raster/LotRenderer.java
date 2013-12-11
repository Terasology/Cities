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

package org.terasology.world.generator.city.raster;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Set;

import org.terasology.world.generator.city.model.Building;
import org.terasology.world.generator.city.model.Lot;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class LotRenderer {

    /**
     * @param g the graphics object
     * @param lots a set of lots
     */
    public void rasterLots(Graphics2D g, Set<Lot> lots) {
        
        g.setColor(Color.BLACK);
        for (Lot lot : lots) {
            g.draw(lot.getShape());
            
            for (Building b : lot.getBuildings()) {
                // tbd
            }
        }
    }

}
