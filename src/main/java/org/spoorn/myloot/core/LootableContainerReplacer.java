package org.spoorn.myloot.core;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spoorn.myloot.block.MyLootBlocks;
import org.spoorn.myloot.block.entity.MyLootChestBlockEntity;

import java.util.ArrayDeque;
import java.util.Queue;

@Log4j2
public class LootableContainerReplacer {
    
    public static Queue<ReplacementInfo> REPLACEMENT_INFOS = new ArrayDeque<>();
    
    public static void init() {
        registerTickCallback();
    }
    
    private static void registerTickCallback() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            ServerWorld serverWorld = server.getWorld(World.OVERWORLD);
            int size = REPLACEMENT_INFOS.size();
            for (int i = 0; i < size; i++) {
                ReplacementInfo replacementInfo = REPLACEMENT_INFOS.remove();
                BlockPos pos = replacementInfo.pos;
                BlockEntity oldBlockEntity = serverWorld.getBlockEntity(pos);

                if (oldBlockEntity instanceof MyLootChestBlockEntity) {
                    continue;
                }

                BlockState oldBlockState = serverWorld.getBlockState(pos);
                //log.info("old block state: " + oldBlockState);
                Block oldBlock = oldBlockState.getBlock();
                if (replacementInfo.lootTableId != null && oldBlock instanceof ChestBlock && serverWorld.isChunkLoaded(pos)) {
                    serverWorld.removeBlockEntity(pos);

                    serverWorld.setBlockState(pos, MyLootBlocks.MY_LOOT_CHEST_BLOCK.getDefaultState().with(ChestBlock.FACING, oldBlockState.get(ChestBlock.FACING)));

                    BlockEntity newBlockEntity = serverWorld.getBlockEntity(pos);
                    //log.info("new block entity: " + newBlockEntity);
                    if (newBlockEntity instanceof MyLootChestBlockEntity myLootChestBlockEntity) {
                        myLootChestBlockEntity.setLootTable(replacementInfo.lootTableId, replacementInfo.lootTableSeed);
                    }
                    //log.info("replaced chest with myLootChest");
                }
            }
        });
    }
    
    @AllArgsConstructor
    public static class ReplacementInfo {
        BlockPos pos;
        Identifier lootTableId;
        long lootTableSeed;
    }
}
