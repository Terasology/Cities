/*
 * Copyright 2014 MovingBlocks
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

package org.terasology.cities.model.roof;

import java.awt.geom.Ellipse2D;

import javax.vecmath.Point2i;

/**
 * A conic (circular base area) roof
 * @author Martin Steiger
 */
public class ConicRoof extends AbstractRoof {

    private int pitch;

    /**
     * @param center
     * @param radius
     * @param baseHeight 
     * @param pitch
     */
    public ConicRoof(Point2i center, int radius, int baseHeight, int pitch) {
        super(new Ellipse2D.Double(center.x - radius, center.y - radius, 2 * radius, 2 * radius), baseHeight);
        this.pitch = pitch;
    }

    @Override
    public Ellipse2D getArea() {
        return (Ellipse2D) super.getArea();
    }

    /**
     * @return the pitch
     */
    public int getPitch() {
        return pitch;
    }
    
}
