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

package org.terasology.cities.contour;


/**
 * Grants access to the 2D arrays in this package
 * @author Martin Steiger
 */
public final class Arrays2D {

    private Arrays2D() {
        // private
    }
    
    /**
     * @param width the width 
     * @param height the height
     * @param border the border thickness around
     * @param initVal the initial value
     * @return the int array
     */
    public static IntArray2D create(int width, int height, int border, byte initVal) {
        return new IntArray2DImpl(width, height, border, initVal);
    }

    /**
     * @param array the underlying array 
     * @param tx the x translation
     * @param ty the y translation
     * @return an instance that uses (x + tx) (y + ty) for access
     */
    public static IntArray2D translate(final IntArray2D array, final int tx, final int ty) {
        return new DelegatingIntArray2D(array) {
            @Override
            public void set(int x, int y, int value) {
                super.set(x + tx, y + ty, value);
            }
            
            @Override
            public int get(int x, int y) {
                return super.get(x + tx, y + ty);
            }
        };
    }

}
