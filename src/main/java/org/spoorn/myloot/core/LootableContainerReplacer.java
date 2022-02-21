package org.spoorn.myloot.core;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spoorn.myloot.block.MyLootBlocks;
import org.spoorn.myloot.block.entity.AbstractMyLootContainerBlockEntity;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Replaces world generated loot containers with myLoot containers if applicable.
 */
@NotThreadSafe
@Log4j2
public class LootableContainerReplacer {
    
    public static Queue<ReplacementInfo> REPLACEMENT_INFOS = new ArrayDeque<>();
    
    public static void init() {
        registerTickCallback();
        registerInstancedLootDrop();
    }

    private static void registerTickCallback() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            int size = REPLACEMENT_INFOS.size();
            for (int i = 0; i < size; i++) {
                ReplacementInfo replacementInfo = REPLACEMENT_INFOS.remove();
                ServerWorld serverWorld = server.getWorld(replacementInfo.worldRegistryKey);
                
                if (serverWorld == null) {
                    continue;
                }
                
                BlockPos pos = replacementInfo.pos;
                BlockEntity oldBlockEntity = serverWorld.getBlockEntity(pos);

                if (oldBlockEntity instanceof AbstractMyLootContainerBlockEntity) {
                    continue;
                }

                BlockState oldBlockState = serverWorld.getBlockState(pos);
                if (replacementInfo.lootTableId != null && oldBlockEntity instanceof ChestBlockEntity && serverWorld.isChunkLoaded(pos)) {
                    serverWorld.removeBlockEntity(pos);

                    // TODO: Handle different block types
                    serverWorld.setBlockState(pos, MyLootBlocks.MY_LOOT_CHEST_BLOCK.getDefaultState().with(ChestBlock.FACING, oldBlockState.get(ChestBlock.FACING)));

                    BlockEntity newBlockEntity = serverWorld.getBlockEntity(pos);
                    if (newBlockEntity instanceof AbstractMyLootContainerBlockEntity myLootContainerBlockEntity) {
                        myLootContainerBlockEntity.setLootTable(replacementInfo.lootTableId, replacementInfo.lootTableSeed);
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
            if (!world.isClient && (entity instanceof AbstractMyLootContainerBlockEntity myLootContainerBlockEntity)) {
                Inventory instancedInventory = myLootContainerBlockEntity.getPlayerInstancedInventory(player);
                if (instancedInventory == null) {
                    log.error("Got null inventory when checking instanced inventory for player={}, entity={}", player, entity);
                } else {
                    ItemScatterer.spawn(world, pos, instancedInventory);
                }
            }
        });
    }
    
    @AllArgsConstructor
    public static class ReplacementInfo {
        RegistryKey<World> worldRegistryKey;
        BlockPos pos;
        Identifier lootTableId;
        long lootTableSeed;
    }
}
