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

package org.terasology.cities.raster;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.cities.BlockTheme;
import org.terasology.cities.BlockTypes;
import org.terasology.math.Side;
import org.terasology.math.geom.Rect2i;
import org.terasology.world.block.Block;
import org.terasology.world.chunks.CoreChunk;

import com.google.common.base.Preconditions;

/**
 * Converts model elements into blocks of of a chunk
 */
public class ChunkRasterTarget implements RasterTarget {

    private static final Logger logger = LoggerFactory.getLogger(ChunkRasterTarget.class);

    private final CoreChunk chunk;
    private final BlockTheme blockType;
    private final Rect2i affectedArea;

    /**
     * @param chunk     the chunk to work on
     * @param blockType a mapping String type to block
     */
    public ChunkRasterTarget(CoreChunk chunk, BlockTheme blockType) {
        this.blockType = blockType;
        this.chunk = chunk;

        int wx = chunk.getChunkWorldOffsetX();
        int wz = chunk.getChunkWorldOffsetZ();
        this.affectedArea = Rect2i.createFromMinAndSize(wx, wz, chunk.getChunkSizeX(), chunk.getChunkSizeZ());
    }

    @Override
    public Rect2i getAffectedArea() {
        return affectedArea;
    }

    @Override
    public int getMaxHeight() {
        return chunk.chunkToWorldPositionY(0) + chunk.getChunkSizeY();
    }

    @Override
    public int getMinHeight() {
        return chunk.chunkToWorldPositionY(0);
    }

    /**
     * @param x    x in world coords
     * @param y    y in world coords
     * @param z    z in world coords
     * @param type the block type
     */
    @Override
    public void setBlock(int x, int y, int z, BlockTypes type) {
        setBlock(x, y, z, blockType.apply(type));
    }

    /**
     * @param x    x in world coords
     * @param y    y in world coords
     * @param z    z in world coords
     * @param type the block type
     */
    @Override
    public void setBlock(int x, int y, int z, BlockTypes type, Set<Side> side) {
        setBlock(x, y, z, blockType.apply(type, side));
    }

    /**
     * @param x     x in world coords
     * @param y     y in world coords
     * @param z     z in world coords
     * @param block the actual block
     */
    protected void setBlock(int x, int y, int z, Block block) {

        int wx = chunk.chunkToWorldPositionX(0);
        int wy = chunk.chunkToWorldPositionY(0);
        int wz = chunk.chunkToWorldPositionZ(0);

        int lx = x - wx;
        int ly = y - wy;
        int lz = z - wz;

        // TODO: remove
        final boolean debugging = true;
        final boolean warnOnly = true;
        if (debugging) {
            boolean xOk = lx >= 0 && lx < chunk.getChunkSizeX();
            boolean yOk = ly >= 0 && ly < chunk.getChunkSizeY();
            boolean zOk = lz >= 0 && lz < chunk.getChunkSizeZ();

            if (warnOnly) {
                if (!xOk) {
                    logger.warn("X value of {} not in range [{}..{}]", x, wx, wx + chunk.getChunkSizeX() - 1);
                    return;
                }

                if (!yOk) {
                    logger.warn("Y value of {} not in range [{}..{}]", y, wy, wy + chunk.getChunkSizeY() - 1);
                    return;
                }

                if (!zOk) {
                    logger.warn("Z value of {} not in range [{}..{}]", z, wz, wz + chunk.getChunkSizeZ() - 1);
                    return;
                }
            } else {
                Preconditions.checkArgument(xOk, "X value of %s not in range [%s..%s]", x, wx, wx + chunk.getChunkSizeX() - 1);
                Preconditions.checkArgument(yOk, "Y value of %s not in range [%s..%s]", y, wy, wy + chunk.getChunkSizeY() - 1);
                Preconditions.checkArgument(zOk, "Z value of %s not in range [%s..%s]", z, wz, wz + chunk.getChunkSizeZ() - 1);

                // an exception will be thrown, so no code below this line will be executed
            }
        }


        chunk.setBlock(lx, ly, lz, block);

    }
}
