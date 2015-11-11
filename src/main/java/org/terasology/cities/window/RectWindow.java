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

package org.terasology.cities.window;

import org.terasology.cities.BlockType;
import org.terasology.cities.DefaultBlockType;
import org.terasology.commonworld.Orientation;
import org.terasology.math.geom.Rect2i;

/**
 * A rectangular window in a wall
 */
public class RectWindow implements Window {

    private final Orientation orientation;
    private Rect2i area;
    private int baseHeight;
    private int topHeight;
    private BlockType blockType;

    /**
     * @param orientation the orientation
     * @param area the window area in the XZ plane
     * @param baseHeight the height at the bottom
     * @param topHeight the height at the bottom
     */
    public RectWindow(Orientation orientation, Rect2i area, int baseHeight, int topHeight) {
        this(orientation, area, baseHeight, topHeight, DefaultBlockType.AIR);
    }

    /**
     * @param orientation the orientation
     * @param area the window area in the XZ plane
     * @param baseHeight the height at the bottom
     * @param topHeight the height at the bottom
     * @param blockType the type of the window
     */
    public RectWindow(Orientation orientation, Rect2i area, int baseHeight, int topHeight, BlockType blockType) {
        this.orientation = orientation;
        this.area = area;
        this.baseHeight = baseHeight;
        this.topHeight = topHeight;
        this.blockType = blockType;
    }

    /**
     * @return the orientation
     */
    public Orientation getOrientation() {
        return this.orientation;
    }

    /**
     * @return the window area
     */
    public Rect2i getArea() {
        return this.area;
    }

    /**
     * @return the baseHeight
     */
    public int getBaseHeight() {
        return this.baseHeight;
    }

    /**
     * @return the height at the top
     */
    public int getTopHeight() {
        return topHeight;
    }

    /**
     * @return the block type
     */
    public BlockType getBlockType() {
        return blockType;
    }
}
