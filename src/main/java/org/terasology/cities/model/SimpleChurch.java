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

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;

/**
 * TODO Type description
 * @author Martin Steiger
 */
public class SimpleChurch implements Building {

    private final Shape layout;
    private final int baseHeight;
    private final int towerHeight;
    private final int hallHeight;
    private final Rectangle towerRect;
    private final Rectangle naveRect;
    private final SimpleDoor door;
    private final Roof naveRoof;
    private final Roof towerRoof;

    /**
     * @param towerRect 
     * @param naveRect 
     * @param door 
     * @param layout
     * @param baseHeight
     * @param naveRoof 
     * @param towerRoof 
     * @param towerHeight
     * @param naveHeight
     */
    public SimpleChurch(Rectangle towerRect, Rectangle naveRect, SimpleDoor door, int baseHeight, Roof naveRoof, Roof towerRoof, int towerHeight, int naveHeight) {
        this.towerRect = towerRect;
        this.naveRect = naveRect;
        this.baseHeight = baseHeight;
        this.naveRoof = naveRoof;
        this.towerRoof = towerRoof;
        this.towerHeight = towerHeight;
        this.hallHeight = naveHeight;
        this.door = door;

        Path2D path = new Path2D.Double();
        path.append(towerRect, false);
        path.append(naveRect, false);
        layout = path;
    }


    @Override
    public Shape getLayout() {
        return layout;
    }


    /**
     * @return the baseHeight
     */
    public int getBaseHeight() {
        return this.baseHeight;
    }


    /**
     * @return the towerHeight
     */
    public int getTowerHeight() {
        return this.towerHeight;
    }


    /**
     * @return the hallHeight
     */
    public int getHallHeight() {
        return this.hallHeight;
    }


    /**
     * @return the towerRect
     */
    public Rectangle getTowerRect() {
        return this.towerRect;
    }


    /**
     * @return the naveRect
     */
    public Rectangle getNaveRect() {
        return this.naveRect;
    }

    /**
     * @return the naveRoof
     */
    public Roof getNaveRoof() {
        return this.naveRoof;
    }


    /**
     * @return the towerRoof
     */
    public Roof getTowerRoof() {
        return this.towerRoof;
    }


    /**
     * @return the door
     */
    public SimpleDoor getDoor() {
        return this.door;
    }

}
