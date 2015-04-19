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

package org.terasology.cities.model;

import java.awt.Rectangle;

import com.google.common.base.Optional;

/**
 * A rectangular {@link Lot}
 */
public class SimpleLot extends Lot {

    private Optional<SimpleFence> fence = Optional.absent();

    /**
     * @param shape the shape of the lot
     */
    public SimpleLot(Rectangle shape) {
        super(shape);
    }

    /**
     * @param fence the fence to set (or <code>null</code> to clear)
     */
    public void setFence(SimpleFence fence) {
        this.fence = Optional.fromNullable(fence);
    }

    @Override
    public Rectangle getShape() {
        return (Rectangle) super.getShape();
    }

    /**
     * @return the fence (if available)
     */
    public Optional<SimpleFence> getFence() {
        return this.fence;
    }

    
}
