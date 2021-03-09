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

package org.terasology.cities.common;

import org.joml.Vector2i;
import org.terasology.commonworld.Orientation;
import org.terasology.commonworld.geom.Line2f;
import org.terasology.engine.world.block.BlockAreac;

/**
 *
 */
public final class Edges {

    private Edges() {
        // no instances
    }

    public static Vector2i getCorner(BlockAreac rc, Orientation o) {

        int dx = o.direction().x() + 1; //  [0..2]
        int dy = o.direction().y() + 1; //  [0..2]

        int x = rc.minX() + (rc.getSizeX() - 1) * dx / 2;
        int y = rc.minY() + (rc.getSizeY() - 1) * dy / 2;

        return new Vector2i(x, y);
    }

    public static int getDistanceToBorder(BlockAreac rc, int x, int z) {
        int rx = x - rc.minX();
        int rz = z - rc.minY();

        // distance to border along both axes
        int borderDistX = Math.min(rx, rc.getSizeX() - 1 - rx);
        int borderDistZ = Math.min(rz, rc.getSizeY() - 1 - rz);

        int dist = Math.min(borderDistX, borderDistZ);
        return dist;
    }

    public static float getDistanceToCorner(BlockAreac rc, int x, int y) {
        return (float) Math.sqrt(getDistanceToCorner(rc, x, y));
    }

    public static int getDistanceToCornerSq(BlockAreac rc, int x, int y) {
        int dx = Math.min(x - rc.minX(), rc.maxX() - x);
        int dy = Math.min(y - rc.minY(), rc.maxY() - y);
        return dx * dx + dy * dy;
    }

    public static Line2f getEdge(BlockAreac rc, Orientation o) {

        Vector2i p0 = getCorner(rc, o.getRotated(-45));
        Vector2i p1 = getCorner(rc, o.getRotated(45));
        return new Line2f(p0.x(), p0.y(), p1.x(), p1.y());
    }
}
