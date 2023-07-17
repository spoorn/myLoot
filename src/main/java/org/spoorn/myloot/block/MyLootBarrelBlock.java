package org.spoorn.myloot.block;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spoorn.myloot.block.entity.MyLootBarrelBlockEntity;


public class MyLootBarrelBlock extends BarrelBlock {
    
    public MyLootBarrelBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MyLootBarrelBlockEntity(pos, state);
    }
}
