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

package org.terasology.cities.bldg;

import org.terasology.cities.model.roof.Roof;
import org.terasology.math.geom.Rect2i;

/**
 *
 */
public class HollowBuildingPart extends AbstractBuildingPart {

    private int arcRadius;

    public HollowBuildingPart(Rect2i layout, Roof roof, int baseHeight, int wallHeight, int arcRadius) {
        super(layout, roof, baseHeight, wallHeight);
        this.arcRadius = arcRadius;
    }

    @Override
    public Rect2i getShape() {
        return (Rect2i) super.getShape();
    }

    public int getArcRadius() {
        return arcRadius;
    }
}
