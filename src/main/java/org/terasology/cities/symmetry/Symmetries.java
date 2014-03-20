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
 * Provides access to different symmetries
 * @author Martin Steiger
 */
public final class Symmetries {
    
    private Symmetries() {
        // avoid instantiation
    }

    /**
     * @return a height map that is mirror along the z axis (0, 1)
     */
    public static Symmetry alongX() {
        return new AbstractSymmetry() {

            @Override
            public boolean isMirrored(int x, int z) {
                return (z < 0);
            }
            
            public Point2i getMirrored(int x, int z) {
                return new Point2i(x, -z - 1);
            }
        };
    }
    
    /**
     * @return a height map that is mirror along the x axis (1, 0)
     */
    public static Symmetry alongZ() {
        return new AbstractSymmetry() {

            @Override
            public boolean isMirrored(int x, int z) {
                return (x < 0);
            }
            
            public Point2i getMirrored(int x, int z) {
                return new Point2i(-x - 1, z);
            }
        };
    }
    
    /**
     * @return a height map that is mirror along the diagonal (1, 1)
     */
    public static Symmetry alongPositiveDiagonal() {
        return new AbstractSymmetry() {

            @Override
            public boolean isMirrored(int x, int z) {
                return (x > z);
            }
            
            @Override
            public Point2i getMirrored(int x, int z) {
                return new Point2i(z, x);
            }
        };
    }
 
    /**
     * @return a height map that is mirror along the diagonal (1, -1)
     */
    public static Symmetry alongNegativeDiagonal() {
        return new AbstractSymmetry() {

            @Override
            public boolean isMirrored(int x, int z) {
                return (x + z < 0);
            }
            
            @Override
            public Point2i getMirrored(int x, int z) {
                int dist = x + z + 1;
                return new Point2i(x - dist, z - dist);
            }
        };
    }
}
