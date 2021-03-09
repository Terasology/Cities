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

package org.terasology.cities.bldg.gen;

import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.terasology.cities.common.Edges;
import org.terasology.commonworld.Orientation;
import org.terasology.engine.world.block.BlockArea;
import org.terasology.engine.world.block.BlockAreac;

/**
 * A turtle has a position and direction. It can be used to define 2D shapes in a relative
 * coordinate system.
 */
public class Turtle {
    private Orientation orient;
    private Vector2i pos;

    public Turtle(Vector2ic pos, Orientation orientation) {
        this.orient = orientation;
        this.pos = new Vector2i(pos);
    }

    /**
     * @param other the turtle to copy
     */
    public Turtle(Turtle other) {
        this(other.pos, other.orient);
    }

    /**
     * @param degrees the rotation (in 45 degree steps)
     * @return this
     */
    public Turtle rotate(int degrees) {
        orient = orient.getRotated(degrees);
        return this;
    }

    /**
     * Sets the position independent of current position/location
     * @param newPos the new coordinates
     * @return this
     */
    public Turtle setPosition(Vector2ic newPos) {
        return setPosition(newPos.x(), newPos.y());
    }

    /**
     * Sets the position independent of current position/location
     * @param x the new x coordinate
     * @param y the new y coordinate
     * @return this
     */
    public Turtle setPosition(int x, int y) {
        pos.set(x, y);
        return this;
    }

    /**
     * Move the turtle relative to the current position and rotation
     * @param right amount to the right
     * @param forward amount forward
     * @return this
     */
    public Turtle move(int right, int forward) {
        Vector2ic dir = orient.direction();
        pos.add(rotateX(dir, right, forward),
            rotateY(dir, right, forward));
        return this;
    }

    /**
     * @param rect the rect to inspect
     * @return the width of the rectangle wrt. the current direction
     */
    public int width(BlockAreac rect) {
        return isHorz() ? rect.getSizeY() : rect.getSizeX();
    }

    /**
     * @param rect the rect to inspect
     * @return the length of the rectangle wrt. the current direction
     */
    public int length(BlockAreac rect) {
        return isHorz() ? rect.getSizeX() : rect.getSizeY();
    }

    /**
     * Creates a rectangle that is centered along the current direction.
     * <pre>
     *      x------x
     * o->  |      |
     *      |      |
     *      x------x
     * </pre>
     * @param right the offset to the right
     * @param forward the offset along the direction axis
     * @param width the width of the rectangle
     * @param len the length of the rectangle
     * @return the rectangle
     */
    public BlockAreac rect(int right, int forward, int width, int len) {
        Vector2ic dir = orient.direction();
        int minX = pos.x() + rotateX(dir, right, forward);
        int minY = pos.y() + rotateY(dir, right, forward);

        int maxX = pos.x() + rotateX(dir, right + width - 1, forward + len - 1);
        int maxY = pos.y() + rotateY(dir, right + width - 1, forward + len - 1);
        return new BlockArea(minX, minY).union(maxX, maxY);
    }

    /**
     * Creates a rectangle that is centered along the current direction.
     * <pre>
     *      x------x
     * o->  |      |
     *      x------x
     * </pre>
     * @param forward the offset along the direction axis
     * @param width the width of the rectangle
     * @param len the length of the rectangle
     * @return the rectangle
     */
    public BlockAreac rectCentered(int forward, int width, int len) {
        return rect(-width / 2, forward, width, len);
    }

    /**
     * @param rc the rectangle to adjust
     * @param left the offset of the left edge
     * @param back the offset of the back edge
     * @param right the offset of the right edge
     * @param forward the offset of the forward edge
     * @return a new rect with adjusted coordinates
     */
    public BlockAreac adjustRect(BlockAreac rc, int left, int back, int right, int forward) {
        Orientation cd = orient.getRotated(45);
        Vector2i max = Edges.getCorner(rc, cd);
        Vector2i min = Edges.getCorner(rc, cd.getOpposite());

        Vector2ic dir = orient.direction();
        int minX = min.x() + rotateX(dir, left, back);
        int minY = min.y() + rotateY(dir, left, back);
        int maxX = max.x() + rotateX(dir, right, forward);
        int maxY = max.y() + rotateY(dir, right, forward);

        return new BlockArea(minX, minY).union(maxX, maxY);
    }

    /**
     * @return the current orientation
     */
    public Orientation getOrientation() {
        return orient;
    }

    /**
     * @return a copy of the current cursor location
     */
    public Vector2ic getPos() {
        return pos;
    }

    /**
     * Apply the current position offset and rotation to the given translation vector
     * @param right amount to the right
     * @param forward amount forward
     * @return the transformed translation
     */
    public Vector2i transform(int right, int forward) {
        int x = pos.x() + rotateX(orient.direction(), right, forward);
        int y = pos.y() + rotateY(orient.direction(), right, forward);
        return new Vector2i(x, y);
    }

    private boolean isHorz() {
        return (orient == Orientation.WEST) || (orient == Orientation.EAST);
    }

    private static int rotateX(Vector2ic dir, int dx, int dy) {
        return -dx * dir.y() + dy * dir.x();
    }

    private static int rotateY(Vector2ic dir, int dx, int dy) {
        return dx * dir.x() + dy * dir.y();
    }
}
