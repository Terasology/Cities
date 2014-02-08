/*
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.cities.swing.draw;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Set;

import org.terasology.cities.model.Lot;
import org.terasology.cities.model.bldg.Building;
import org.terasology.cities.model.bldg.SimpleChurch;
import org.terasology.cities.model.bldg.SimpleHome;

/**
 * Draws lots and the contained buildings
 * @author Martin Steiger
 */
public class LotRenderer {

    /**
     * @param g the graphics object
     * @param lots a set of lots
     */
    public void rasterLots(Graphics2D g, Set<Lot> lots) {
        
        for (Lot lot : lots) {
            g.setColor(Color.YELLOW);
            g.draw(lot.getShape());
            
            for (Building b : lot.getBuildings()) {
                
                if (b instanceof SimpleHome) {
                    SimpleHome sb = (SimpleHome) b;

                    g.setColor(Color.RED);
                    g.fill(b.getLayout());
                    g.setColor(Color.RED.darker());
                    g.draw(b.getLayout());

                    g.setColor(Color.GREEN);
                    g.draw(sb.getDoor().getRect());
                }
                
                if (b instanceof SimpleChurch) {
                    SimpleChurch sc = (SimpleChurch) b;
                    g.setColor(Color.GREEN);
                    g.fill(b.getLayout());
                    g.setColor(Color.GREEN.darker());
                    g.draw(b.getLayout());
                    g.setColor(Color.BLUE);
                    g.draw(sc.getDoor().getRect());
                }
            }
        }
    }

}
