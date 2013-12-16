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

package org.terasology.cities.testing.swing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Set;

import org.terasology.cities.model.SimpleBuilding;
import org.terasology.cities.model.SimpleLot;

/**
 * Draws lots and the contained buildings
 * @author Martin Steiger
 */
public class LotRenderer {

    /**
     * @param g the graphics object
     * @param lots a set of lots
     */
    public void rasterLots(Graphics2D g, Set<SimpleLot> lots) {
        
        for (SimpleLot lot : lots) {
            g.setColor(Color.YELLOW);
            g.draw(lot.getShape());
            
            for (SimpleBuilding b : lot.getBuildings()) {
                g.setColor(Color.RED);
                g.fill(b.getLayout());
                g.setColor(Color.RED.darker());
                g.draw(b.getLayout());
                g.setColor(Color.GREEN);
                g.draw(b.getDoor());
            }
        }
    }

}
