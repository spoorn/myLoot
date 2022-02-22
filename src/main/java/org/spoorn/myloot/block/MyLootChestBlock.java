package org.spoorn.myloot.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.spoorn.myloot.block.entity.MyLootChestBlockEntity;
import org.spoorn.myloot.entity.MyLootEntities;

public class MyLootChestBlock extends ChestBlock {
    
    public MyLootChestBlock(Settings settings) {
        super(settings, () -> MyLootEntities.MY_LOOT_CHEST_BLOCK_ENTITY_TYPE);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MyLootChestBlockEntity(pos, state);
    }
}
