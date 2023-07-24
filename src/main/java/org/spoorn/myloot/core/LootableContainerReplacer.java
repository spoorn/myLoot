package org.spoorn.myloot.core;

import lombok.extern.log4j.Log4j2;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import org.spoorn.myloot.block.entity.MyLootContainer;
import org.spoorn.myloot.config.BlockMapping;
import org.spoorn.myloot.config.ModConfig;
import org.spoorn.myloot.util.MyLootUtil;
import oshi.annotation.concurrent.NotThreadSafe;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * Replaces world generated loot containers with myLoot containers if applicable.
 */
@NotThreadSafe
@Log4j2
public class LootableContainerReplacer {
    
    public static Queue<ReplacementInfo> REPLACEMENT_INFOS = new ArrayDeque<>();
    public static final Map<String, Block> BLOCK_REVERSE_MAPPING = new HashMap<>();
    private static final Map<Pattern, Block> COMPILED_PATTERNS = new HashMap<>();
    
    // Use our own loaded chunks cache as serverWorld.isChunkLoaded() has a bunch of checks we can skip
    // This will increase memory a bit, but performance should improve
    private static final Map<RegistryKey<World>, Set<ChunkPos>> LOADED_CHUNKS_CACHE = new HashMap<>();
    
    public static void init() {
        registerChunkLoadCache();
        registerTickCallback();
        registerInstancedLootDrop();
        for (BlockMapping blockMapping : ModConfig.get().blockMapping) {
            String type = blockMapping.myLootType;
            for (String block : blockMapping.replaces) {
                if (BLOCK_REVERSE_MAPPING.containsKey(block)) {
                    log.error("myLoot blockMapping contains duplicate mappings for block={}", block);
                    throw new RuntimeException("myLoot blockMapping contains duplicate mappings for block=" + block);
                } else {
                    Block myLootBlock = MyLootUtil.getMyLootBlockFromName(type);
                    BLOCK_REVERSE_MAPPING.put(block, myLootBlock);
                    COMPILED_PATTERNS.put(Pattern.compile(block), myLootBlock);
                }
            }
        }
    }
    
    private static void registerChunkLoadCache() {
        ServerChunkEvents.CHUNK_LOAD.register((serverWorld, chunk) -> {
            RegistryKey<World> dimension = serverWorld.getRegistryKey();
            // Use same chunk status as serverWorld.isChunkLoaded
            if (!ModConfig.get().disabledDimensions.contains(dimension.toString()) && chunk.getStatus().isAtLeast(ChunkStatus.FULL)) {
                LOADED_CHUNKS_CACHE.computeIfAbsent(dimension, m -> new HashSet<>()).add(chunk.getPos());
            }
        });
        
        ServerChunkEvents.CHUNK_UNLOAD.register((serverWorld, chunk) -> {
            RegistryKey<World> dimension = serverWorld.getRegistryKey();
            // Use same chunk status as serverWorld.isChunkLoaded
            if (LOADED_CHUNKS_CACHE.containsKey(dimension)) {
                LOADED_CHUNKS_CACHE.get(dimension).remove(chunk.getPos());
            }
        });
    }

    private static void registerTickCallback() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            int size = REPLACEMENT_INFOS.size();
            for (int i = 0; i < size; i++) {
                ReplacementInfo replacementInfo = REPLACEMENT_INFOS.remove();
                
                if (replacementInfo.lootTableId == null) {
                    continue;
                }
                
                if (ModConfig.get().disabledDimensions.contains(replacementInfo.worldRegistryKey.getValue().toString())
                    || ModConfig.get().disabledLootTables.contains(replacementInfo.lootTableId.toString())) {
                    continue;
                }
                
                if (!LOADED_CHUNKS_CACHE.containsKey(replacementInfo.worldRegistryKey) || !LOADED_CHUNKS_CACHE.get(replacementInfo.worldRegistryKey).containsAll(replacementInfo.chunkPos)) {
                    // Add back to the queue to retry later when chunk gets loaded
                    REPLACEMENT_INFOS.add(replacementInfo);
                    continue;
                }
                
                ServerWorld serverWorld = server.getWorld(replacementInfo.worldRegistryKey);
                
                if (serverWorld == null) {
                    continue;
                }
                
                BlockPos pos = replacementInfo.pos;
                BlockEntity oldBlockEntity = serverWorld.getBlockEntity(pos);

                // Sanity checks, should have been caught earlier
                if (!(oldBlockEntity instanceof LootableContainerBlockEntity) || oldBlockEntity instanceof MyLootContainer) {
                    continue;
                }

                BlockState oldBlockState = serverWorld.getBlockState(pos);
                
                if (MyLootUtil.supportedEntity(oldBlockEntity)) {
                    String blockName = MyLootUtil.getBlockName(oldBlockState.getBlock());
                    Block replacementBlock = getReplacementBlockIfSupported(blockName);
                    
                    if (replacementBlock == null) {
                        log.warn("MyLoot replacer does not support " + blockName + ", skipping");
                        continue;
                    }
                    
                    // The below must be done on the server thread, as updating chunks is only done on the server main thread
                    // so putting this on a separate thread would be extremely slow as it just queues updates for the main thread and waits.
                    serverWorld.removeBlockEntity(pos);

                    BlockState newBlockState = replacementBlock.getDefaultState();
                    // Unchecked to workaround generics and wildcards
                    // Copy blockState properties of original block to replacement
                    for (Property property : replacementInfo.originalBlockState.getProperties()) {
                        newBlockState = newBlockState.withIfExists(property, replacementInfo.originalBlockState.get(property));
                    }
                    serverWorld.setBlockState(pos, newBlockState, Block.NOTIFY_ALL);

                    BlockEntity newBlockEntity = serverWorld.getBlockEntity(pos);
                    if (newBlockEntity instanceof MyLootContainer myLootContainer) {
                        myLootContainer.setMyLootLootTable(replacementInfo.lootTableId, replacementInfo.lootTableSeed);
                    }
                }
            }
        });
    }

    /**
     * Drop loot based on which player broke a myLoot container.
     */
    private static void registerInstancedLootDrop() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
            if (!world.isClient && (entity instanceof MyLootContainer myLootContainer)) {
                Inventory instancedInventory = myLootContainer.getPlayerInstancedInventory(player);
                if (instancedInventory == null) {
                    //log.error("Got null inventory when checking instanced inventory for player={}, entity={}", player, entity);
                } else if (MyLootUtil.PLAYER_INSTANCE_DROP_BEHAVIOR.equals(ModConfig.get().dropBehavior)) {
                    ItemScatterer.spawn(world, pos, instancedInventory);
                }
            }
        });
    }
    
    private static Block getReplacementBlockIfSupported(String blockName) {
        if (BLOCK_REVERSE_MAPPING.containsKey(blockName)) {
            return BLOCK_REVERSE_MAPPING.get(blockName);
        }
        
        for (Entry<Pattern, Block> entry : COMPILED_PATTERNS.entrySet()) {
            if (entry.getKey().matcher(blockName).matches()) {
                return entry.getValue();
            }
        }
        
        return null;
    }
    
    public static class ReplacementInfo {
        RegistryKey<World> worldRegistryKey;
        BlockPos pos;
        Set<ChunkPos> chunkPos;
        Identifier lootTableId;
        long lootTableSeed;
        // When we replace blocks, since we update neighbor states on each block replacement, it can change the
        // neighboring block states which can result in different behavior after replacement.  For example, when we
        // replace one side of a double chest, it will reset the other double chest to a single chest because the
        // block types are different.  We want to preserve properties here
        BlockState originalBlockState;

        public ReplacementInfo(RegistryKey<World> worldRegistryKey, BlockPos pos, Identifier lootTableId, long lootTableSeed, BlockState blockState) {
            this.worldRegistryKey = worldRegistryKey;
            this.pos = pos;
            this.lootTableId = lootTableId;
            this.lootTableSeed = lootTableSeed;
            this.chunkPos = new HashSet<>();
            
            ChunkPos center = new ChunkPos(this.pos);
            this.chunkPos.add(center);

            // We mark the current chunk this block to be replaced is at, along with its neighbors because during
            // the replacement logic, the setBlockState() triggers updateNeighbors() which may require neighboring
            // chunks to be updated.  We have a check in the replacer for whether chunks are loaded before trying to
            // replace with myLoot containers, so we also make sure the neighboring chunks are loaded as well, else
            // setBlockState() will wait for those neighboring chunks to load on the server thread, causing lower 
            // server TPS as replacement logic is done on the main server thread
            // Set this to 2 radius instead of 1, might help with https://github.com/spoorn/myLoot/issues/26
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    chunkPos.add(new ChunkPos(center.x + x, center.z + z));
                }
            }
            
            this.originalBlockState = blockState;
        }
    }
}
