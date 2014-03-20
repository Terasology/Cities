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

package org.terasology.cities.symmetry;

import javax.vecmath.Point2i;


/**
 * An abstract partial implementation of {@link Symmetry}
 * @author Martin Steiger
 */
public abstract class AbstractSymmetry implements Symmetry {

    @Override
    public boolean isMirrored(Point2i v) {
        return isMirrored(v.x, v.y);
    }
    
    @Override
    public Point2i getMirrored(Point2i v) {
        return getMirrored(v.x, v.y);
    }

}
