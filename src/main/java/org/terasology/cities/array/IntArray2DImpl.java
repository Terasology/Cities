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

import java.util.Arrays;

/**
 * Defines a 2D array with a padding border
 * @author Martin Steiger
 */
class IntArray2DImpl implements IntArray2D {
    private final int border;
    
    private final int[][] data;

    private final int width;
    private final int height;
    
    /**
     * @param width the width 
     * @param height the height
     * @param border the border thickness around
     * @param initVal the initial value
     */
    public IntArray2DImpl(int width, int height, int border, int initVal) {
        data = new int[height + border * 2][width + border * 2];
        this.width = width;
        this.height = height;
        
        if (initVal != 0) {
            for (int[] row : data) {
                Arrays.fill(row, initVal);
            }
        }
        
        this.border = border;
    }

    /**
     * @param x the x coord
     * @param y the y coord
     * @param value the value
     */
    @Override
    public void set(int x, int y, int value) {
        data[y + border][x + border] = value;
    }

    /**
     * @param x the x coord
     * @param y the y coord
     * @return the value
     */
    @Override
    public int get(int x, int y) {
        return data[y + border][x + border];
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

}
