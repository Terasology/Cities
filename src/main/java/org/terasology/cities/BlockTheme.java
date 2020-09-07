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

package org.terasology.cities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.math.Side;
import org.terasology.math.SideBitFlag;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.BlockUri;
import org.terasology.world.block.family.BlockFamily;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * A mapping from block types (as defined in {@link BlockType}) to actual blocks
 */
public final class BlockTheme implements Function<BlockType, Block> {

    private static final Logger logger = LoggerFactory.getLogger(BlockTheme.class);

    private final Map<BlockType, Block> blockMap;
    private final Map<BlockType, BlockFamily> familyMap;

    private final BlockFamily defaultFamily;
    private final Block defaultBlock;

    private BlockTheme(Map<BlockType, Block> blocks, Block defBlock,
            Map<BlockType, BlockFamily> families, BlockFamily defFamily) {
        this.blockMap = new HashMap<>(blocks);
        this.defaultBlock = defBlock;
        this.familyMap = new HashMap<>(families);
        this.defaultFamily = defFamily;
    }

    public static Builder builder(BlockManager blockManager) {
        return new Builder(blockManager);
    }

    @Override
    public Block apply(BlockType input) {

        Block block = blockMap.get(input);

        if (block == null) {
            block = defaultBlock;
            logger.warn("Could not resolve block type \"{}\" - using default", input);
        }

        return block;
    }

    /**
     * @param input the block type
     * @param sides the connected sides
     * @return the block
     */
    public Block apply(BlockType input, Set<Side> sides) {

        BlockFamily family = familyMap.get(input);

        if (family == null) {
            family = defaultFamily;
            logger.warn("Could not resolve block type \"{}\" - using default", input);
        }

        BlockUri familyUri = family.getURI().getFamilyUri();
        Block block = null;
        if (sides.size() == 1) {
            Side side = sides.iterator().next();
            BlockUri blockUri = new BlockUri(familyUri + BlockUri.IDENTIFIER_SEPARATOR + side);
            block = family.getBlockFor(blockUri);
        }

        if (block == null) {
            byte flags = SideBitFlag.getSides(sides);
            BlockUri blockUri = new BlockUri(familyUri + BlockUri.IDENTIFIER_SEPARATOR + flags);
            block = family.getBlockFor(blockUri);
        }

        if (block == null) {
            block = family.getArchetypeBlock();
        }

        return block;
    }

    public static final class Builder {
        private final BlockManager blockManager;

        private final Block defaultBlock;
        private final BlockFamily defaultFamily;

        private final Map<BlockType, Block> blockMap = new HashMap<>();
        private final Map<BlockType, BlockFamily> familyMap = new HashMap<>();

        private Builder(BlockManager blockManager) {
            this.blockManager = blockManager;
            blockMap.put(DefaultBlockType.AIR, blockManager.getBlock(BlockManager.AIR_ID));
            defaultBlock = blockManager.getBlock(BlockManager.UNLOADED_ID);
            defaultFamily = blockManager.getBlockFamily(BlockManager.UNLOADED_ID);
        }

        public BlockTheme build() {
            return new BlockTheme(blockMap, defaultBlock, familyMap, defaultFamily);
        }

        /**
         * @param blockType the block type (as defined in BlockTypes}
         * @param blockUri the qualified block uri (modulename:id)
         * @return this
         */
        public Builder register(BlockType blockType, String blockUri) {
            register(blockType, new BlockUri(blockUri));
            return this;
        }

        /**
         * @param blockType the block type (as defined in BlockTypes}
         * @param blockUri the block uri
         * @return this
         */
        public Builder register(BlockType blockType, BlockUri blockUri) {
            Block block = blockManager.getBlock(blockUri);

            if (!BlockManager.AIR_ID.equals(blockUri)) {
                if (block == null || block.equals(blockManager.getBlock(BlockManager.AIR_ID))) {
                    logger.warn("Could not resolve block URI \"{}\" - using default", blockUri);
                    block = defaultBlock;
                }
            }

            blockMap.put(blockType, block);
            return this;
        }

        /**
         * @param blockType the block type (as defined in BlockTypes}
         * @param blockUri the qualified block family uri (modulename:id)
         * @return this
         */
        public Builder registerFamily(BlockType blockType, String blockUri) {
            registerFamily(blockType, new BlockUri(blockUri));
            return this;
        }

        /**
         * @param blockType the block type (as defined in BlockTypes}
         * @param blockUri the block family uri
         * @return this
         */
        public Builder registerFamily(BlockType blockType, BlockUri blockUri) {
            BlockFamily family = blockManager.getBlockFamily(blockUri);

            if (family == null) {
                logger.warn("Could not resolve block URI \"{}\" - using default", blockUri);
                family = defaultFamily;
            }

            familyMap.put(blockType, family);
            return this;
        }
    }
}
