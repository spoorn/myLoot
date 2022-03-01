package org.spoorn.myloot.core;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BarrelBlock;
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
import org.spoorn.myloot.block.entity.MyLootContainerBlockEntity;
import org.spoorn.myloot.config.ModConfig;
import org.spoorn.myloot.util.MyLootUtil;

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

                if (oldBlockEntity instanceof MyLootContainerBlockEntity) {
                    continue;
                }

                BlockState oldBlockState = serverWorld.getBlockState(pos);
                if (replacementInfo.lootTableId != null && MyLootUtil.supportedEntity(oldBlockEntity) && serverWorld.isChunkLoaded(pos)) {
                    serverWorld.removeBlockEntity(pos);
                    
                    if (oldBlockState.getBlock() instanceof ChestBlock) {
                        serverWorld.setBlockState(pos, MyLootBlocks.MY_LOOT_CHEST_BLOCK.getDefaultState().with(ChestBlock.FACING, oldBlockState.get(ChestBlock.FACING)));
                    } else if (oldBlockState.getBlock() instanceof BarrelBlock) {
                        serverWorld.setBlockState(pos, MyLootBlocks.MY_LOOT_BARREL_BLOCK.getDefaultState().with(Properties.FACING, oldBlockState.get(Properties.FACING)));
                    }

                    BlockEntity newBlockEntity = serverWorld.getBlockEntity(pos);
                    if (newBlockEntity instanceof MyLootContainerBlockEntity myLootContainerBlockEntity) {
                        myLootContainerBlockEntity.setMyLootLootTable(replacementInfo.lootTableId, replacementInfo.lootTableSeed);
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
            if (!world.isClient && (entity instanceof MyLootContainerBlockEntity myLootContainerBlockEntity)) {
                Inventory instancedInventory = myLootContainerBlockEntity.getPlayerInstancedInventory(player);
                if (instancedInventory == null) {
                    //log.error("Got null inventory when checking instanced inventory for player={}, entity={}", player, entity);
                } else if (MyLootUtil.PLAYER_INSTANCE_DROP_BEHAVIOR.equals(ModConfig.get().dropBehavior)) {
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
