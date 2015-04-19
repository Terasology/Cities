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

package org.terasology.cities.model.bldg;

import java.awt.Rectangle;

import org.terasology.cities.model.roof.Roof;

/**
 * Defines a rectangular building part
 */
public class SimpleBuildingPart implements BuildingPart {

    private final Rectangle layout;
    private final int baseHeight;
    private final int topHeight;
    private final Roof roof;
    
    /**
     * @param layout the floor layout
     * @param baseHeight the base height
     * @param topHeight the top height (==roof base height)
     * @param roof the roof type
     */
    public SimpleBuildingPart(Rectangle layout, int baseHeight, int topHeight, Roof roof) {
        this.layout = layout;
        this.baseHeight = baseHeight;
        this.topHeight = topHeight;
        this.roof = roof;
    }

    /**
     * @return the layout
     */
    @Override
    public Rectangle getLayout() {
        return this.layout;
    }
    
    /**
     * @return the baseHeight
     */
    public int getBaseHeight() {
        return this.baseHeight;
    }
    
    /**
     * @return the topHeight
     */
    public int getTopHeight() {
        return this.topHeight;
    }
    
    /**
     * @return the roof
     */
    public Roof getRoof() {
        return this.roof;
    }
}
