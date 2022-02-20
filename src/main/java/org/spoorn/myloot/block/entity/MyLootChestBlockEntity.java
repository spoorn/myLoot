package org.spoorn.myloot.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.spoorn.myloot.entity.MyLootEntities;
import org.spoorn.myloot.inventory.MyLootInventory;

import java.util.HashMap;
import java.util.Map;

public class MyLootChestBlockEntity extends ChestBlockEntity {
    
    private Map<String, DefaultedList<ItemStack>> defaultInventories = new HashMap<>();
    private Map<String, MyLootInventory> inventories = new HashMap<>();
    
    public MyLootChestBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(MyLootEntities.MY_LOOT_CHEST_BLOCK_ENTITY_BLOCK_ENTITY_TYPE, blockPos, blockState);
    }

    @Override
    protected Text getContainerName() {
        return new LiteralText("myLootChestBlock");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        PlayerEntity player = playerInventory.player;
        String playerId = player.getGameProfile().getId().toString();
        System.out.println("### playerId: " + playerId);
        Inventory inventory = this.inventories.computeIfAbsent(playerId, m -> new MyLootInventory(this.getInvStackList()));
        return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, inventory);
    }
}
