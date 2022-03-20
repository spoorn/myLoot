package org.spoorn.myloot.core;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spoorn.myloot.block.MyLootBlocks;
import org.spoorn.myloot.block.entity.MyLootContainer;
import org.spoorn.myloot.config.BlockMapping;
import org.spoorn.myloot.config.ModConfig;
import org.spoorn.myloot.util.MyLootUtil;

import javax.annotation.concurrent.NotThreadSafe;
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
    
    public static void init() {
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

    private static void registerTickCallback() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            int size = REPLACEMENT_INFOS.size();
            for (int i = 0; i < size; i++) {
                ReplacementInfo replacementInfo = REPLACEMENT_INFOS.remove();
                
                if (ModConfig.get().disabledDimensions.contains(replacementInfo.worldRegistryKey.getValue().toString())
                    || ModConfig.get().disabledLootTables.contains(replacementInfo.lootTableId.toString())) {
                    continue;
                }
                
                ServerWorld serverWorld = server.getWorld(replacementInfo.worldRegistryKey);
                
                if (serverWorld == null) {
                    continue;
                }
                
                BlockPos pos = replacementInfo.pos;
                BlockEntity oldBlockEntity = serverWorld.getBlockEntity(pos);

                if (oldBlockEntity instanceof MyLootContainer) {
                    continue;
                }

                BlockState oldBlockState = serverWorld.getBlockState(pos);
                
                if (replacementInfo.lootTableId != null && MyLootUtil.supportedEntity(oldBlockEntity) && serverWorld.isChunkLoaded(pos)) {
                    String blockName = MyLootUtil.getBlockName(oldBlockState.getBlock());
                    Block replacementBlock = getReplacementBlockIfSupported(blockName);
                    
                    if (replacementBlock == null) {
                        log.warn("MyLoot replacer does not support " + blockName + ", skipping");
                        continue;
                    }
                    
                    serverWorld.removeBlockEntity(pos);

                    if (replacementBlock == MyLootBlocks.MY_LOOT_CHEST_BLOCK) {
                        // Chest blocks have a different property
                        serverWorld.setBlockState(pos, MyLootBlocks.MY_LOOT_CHEST_BLOCK.getDefaultState().with(ChestBlock.FACING, oldBlockState.get(ChestBlock.FACING)));
                    } else {
                        serverWorld.setBlockState(pos, replacementBlock.getDefaultState().with(Properties.FACING, oldBlockState.get(Properties.FACING)));
                    }

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
    
    @AllArgsConstructor
    public static class ReplacementInfo {
        RegistryKey<World> worldRegistryKey;
        BlockPos pos;
        Identifier lootTableId;
        long lootTableSeed;
    }
}
