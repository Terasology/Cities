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

package org.terasology.cities.array;

/**
 * A 2D array based on ints
 * @author Martin Steiger
 */
public interface IntArray2D {

    /**
     * @param x the x coord
     * @param y the y coord
     * @param value the value
     */
    void set(int x, int y, int value);

    /**
     * @param x the x coord
     * @param y the y coord
     * @return the value
     */
    int get(int x, int y);

    /**
     * @return the width of the array
     */
    int getWidth();
    
    /**
     * @return the height of the array
     */
    int getHeight();
}

