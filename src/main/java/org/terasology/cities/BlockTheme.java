/*
 * Copyright 2013 MovingBlocks
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

package org.terasology.cities;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.CoreRegistry;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

/**
 * A mapping from block types (as defined in {@link BlockTypes}) to actual blocks
 * @author Martin Steiger
 */
final class BlockTheme implements Function<BlockTypes, Block> {

    private static final Logger logger = LoggerFactory.getLogger(BlockTheme.class);

    private final Map<BlockTypes, Block> map = Maps.newConcurrentMap();
    private final BlockManager blockManager = CoreRegistry.get(BlockManager.class);
    private final Block defaultBlock;

    /**
     * Setup the mapping with defaults 
     */
    public BlockTheme() {
        map.put(BlockTypes.AIR, BlockManager.getAir());
        defaultBlock = blockManager.getBlock("core:Stone");
    }

    /**
     * @param blockType the block type (as defined in BlockTypes} 
     * @param blockUri the block uri
     */
    public void register(BlockTypes blockType, String blockUri) {
        Block block = blockManager.getBlock(blockUri);
        
        if (block == null) {
            logger.warn("Could not resolve block URI \"{}\" - skipping", blockUri);
        } else {
            map.put(blockType, block);
        }
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
}
