package org.spoorn.myloot.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spoorn.myloot.block.entity.MyLootChestBlockEntity;
import org.spoorn.myloot.entity.MyLootEntities;

import java.util.Optional;

public class MyLootChestBlock extends ChestBlock {

    private static final DoubleBlockProperties.PropertyRetriever<ChestBlockEntity, Optional<NamedScreenHandlerFactory>> NAME_RETRIEVER = new DoubleBlockProperties.PropertyRetriever<>(){

        @Override
        public Optional<NamedScreenHandlerFactory> getFromBoth(final ChestBlockEntity chestBlockEntity, final ChestBlockEntity chestBlockEntity2) {
            if (!(chestBlockEntity instanceof MyLootChestBlockEntity myLootChestBlockEntity) || !(chestBlockEntity2 instanceof MyLootChestBlockEntity myLootChestBlockEntity2)) {
                throw new RuntimeException("MyLoot Chest can only be paired with another MyLoot chest!");
            }
            return Optional.of(new NamedScreenHandlerFactory(){

                @Override
                @Nullable
                public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                    if (chestBlockEntity.checkUnlocked(playerEntity) && chestBlockEntity2.checkUnlocked(playerEntity)) {
                        chestBlockEntity.checkLootInteraction(playerInventory.player);
                        chestBlockEntity2.checkLootInteraction(playerInventory.player);
                        DoubleInventory inventory = new DoubleInventory(myLootChestBlockEntity.getPlayerInstancedInventory(playerEntity),
                                myLootChestBlockEntity2.getPlayerInstancedInventory(playerEntity));
                        return GenericContainerScreenHandler.createGeneric9x6(i, playerInventory, inventory);
                    }
                    return null;
                }

                @Override
                public Text getDisplayName() {
                    if (chestBlockEntity.hasCustomName()) {
                        return chestBlockEntity.getDisplayName();
                    }
                    if (chestBlockEntity2.hasCustomName()) {
                        return chestBlockEntity2.getDisplayName();
                    }
                    return Text.translatable("myloot.container.doubleChest");
                }
            });
        }

        @Override
        public Optional<NamedScreenHandlerFactory> getFrom(ChestBlockEntity chestBlockEntity) {
            return Optional.of(chestBlockEntity);
        }

        @Override
        public Optional<NamedScreenHandlerFactory> getFallback() {
            return Optional.empty();
        }
    };
    
    public MyLootChestBlock(Settings settings) {
        super(settings, () -> MyLootEntities.MY_LOOT_CHEST_BLOCK_ENTITY_TYPE);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MyLootChestBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return this.getBlockEntitySource(state, world, pos, false).apply(NAME_RETRIEVER).orElse(null);
    }
}
