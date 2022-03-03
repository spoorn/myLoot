package org.spoorn.myloot.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spoorn.myloot.block.entity.MyLootShulkerBoxBlockEntity;
import org.spoorn.myloot.entity.MyLootEntities;

import java.util.List;

public class MyLootShulkerBoxBlock extends ShulkerBoxBlock {
    
    private final DyeColor color;
    
    public MyLootShulkerBoxBlock(@Nullable DyeColor color, Settings settings) {
        super(color, settings);
        this.color = color;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MyLootShulkerBoxBlockEntity(this.color, pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return ShulkerBoxBlock.checkType(type, MyLootEntities.MY_LOOT_SHULKER_BOX_BLOCK_ENTITY_TYPE, ShulkerBoxBlockEntity::tick);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        ItemStack itemStack = new ItemStack(this);
        world.getBlockEntity(pos, MyLootEntities.MY_LOOT_SHULKER_BOX_BLOCK_ENTITY_TYPE).ifPresent(blockEntity -> blockEntity.setStackNbt(itemStack));
        return itemStack;
    }

    // TODO: override
    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        return super.getDroppedStacks(state, builder);
    }
}
