package org.spoorn.myloot.block.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;

public interface MyLootContainerBlockEntity {

    Text getContainerName();

    void setLootTable(Identifier id, long seed);

    /**
     * This should be called when various container entities are first supplied with generated loot.
     */
    void setDefaultLoot();
    
    void onOpen(PlayerEntity player);

    void onClose(PlayerEntity player);

    void markDirty();

    boolean canPlayerUse(PlayerEntity player);

    boolean hasPlayerOpened(PlayerEntity player);

    @Nullable
    Inventory getPlayerInstancedInventory(PlayerEntity player);
}
