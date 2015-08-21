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

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.math.Side;
import org.terasology.math.SideBitFlag;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.BlockUri;
import org.terasology.world.block.family.BlockFamily;

import java.util.Map;
import java.util.Set;

/**
 * A mapping from block types (as defined in {@link BlockTypes}) to actual blocks
 */
public final class BlockTheme implements Function<BlockTypes, Block> {

    private static final Logger logger = LoggerFactory.getLogger(BlockTheme.class);

    private final Map<BlockTypes, Block> map = Maps.newConcurrentMap();
    private final Map<BlockTypes, BlockFamily> familyMap = Maps.newConcurrentMap();
    private final BlockManager blockManager = CoreRegistry.get(BlockManager.class);
    private final Block defaultBlock;
    private final BlockFamily defaultFamily;

    /**
     * Setup the mapping with defaults
     */
    public BlockTheme() {
        map.put(BlockTypes.AIR, blockManager.getBlock(BlockManager.AIR_ID));
        defaultBlock = blockManager.getBlock(BlockManager.UNLOADED_ID);
        defaultFamily = blockManager.getBlockFamily(BlockManager.UNLOADED_ID);
    }

    /**
     * @param blockType the block type (as defined in BlockTypes}
     * @param blockUri the block uri
     */
    public void register(BlockTypes blockType, String blockUri) {
        Block block = blockManager.getBlock(blockUri);

        if (block == null || block.equals(blockManager.getBlock(BlockManager.AIR_ID))) {
            logger.warn("Could not resolve block URI \"{}\" - using default", blockUri);
            block = defaultBlock;
        }

        map.put(blockType, block);
    }

    /**
     * @param blockType the block type (as defined in BlockTypes}
     * @param blockUri the block uri
     */
    public void registerFamily(BlockTypes blockType, String blockUri) {
        BlockFamily block = blockManager.getBlockFamily(blockUri);

        if (block == null) {
            logger.warn("Could not resolve block URI \"{}\" - using default", blockUri);
            block = defaultFamily;
        }

        familyMap.put(blockType, block);
    }

    /**
     * Remove blockType from the mapping
     * @param blockType the block type (as defined in BlockTypes}
     */
    public void unregister(String blockType) {
        map.remove(blockType);
    }

    @Override
    public Block apply(BlockTypes input) {

        Block block = map.get(input);

        if (block == null) {
            block = defaultBlock;
            logger.warn("Could not resolve block type \"{}\" - using default", input);
        }

        return block;
    }

    /**
     * @param input the block type
     * @param side the connected sides
     * @return the block
     */
    public Block apply(BlockTypes input, Set<Side> side) {

        BlockFamily family = familyMap.get(input);

        if (family == null) {
            family = defaultFamily;
            logger.warn("Could not resolve block type \"{}\" - using default", input);
        }

        BlockUri familyUri = family.getURI().getFamilyUri();
        String identifier = family.getURI().getIdentifier().toString();
        byte flags = SideBitFlag.getSides(side);
        BlockUri blockUri = new BlockUri(familyUri + ":" + identifier + flags);
        Block block = family.getBlockFor(blockUri);

        if (block == null) {
            block = family.getArchetypeBlock();
        }

        return block;
    }
}
