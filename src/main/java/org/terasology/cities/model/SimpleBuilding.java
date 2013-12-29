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

package org.terasology.cities.model;

import java.awt.Rectangle;

/**
 * Defines a rectangular shaped building in the most common sense
 * @author Martin Steiger
 */
public class SimpleBuilding extends AbstractBuilding {

    /**
     * @param layout the building layout
     * @param roof the roof definition
     * @param baseHeight the height of the floor level
     * @param wallHeight the building height above the floor level
     */
    public SimpleBuilding(Rectangle layout, Roof roof, int baseHeight, int wallHeight) {
        super(layout, roof, baseHeight, wallHeight);
    }
    
    @Override
    public Rectangle getLayout() {
        return (Rectangle) super.getLayout();
    }

}
