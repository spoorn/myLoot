package org.spoorn.myloot.block.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface MyLootContainer {

    Text getContainerName();

    @Nullable
    World getMyLootWorld();
    
    @Nullable
    default BlockPos getBlockPos() {
        return null;
    }
    
    @Nullable
    default Vec3d getEntityPos() {
        return null;
    }
    
    Identifier getOriginalLootTableIdentifier();
    
    void setMyLootLootTable(Identifier id, long seed);
    
    DefaultedList<ItemStack> getOriginalInventory();
    
    DefaultedList<ItemStack> getDefaultLoot();

    /**
     * This should be called when various container entities are first supplied with generated loot.
     */
    void setDefaultLoot();
    
    void onOpen(PlayerEntity player);

    void onClose(PlayerEntity player);

    void markDirty();

    boolean canPlayerUse(PlayerEntity player);

    boolean hasBeenOpened();
    
    boolean hasPlayerOpened(PlayerEntity player);

    @Nullable
    Inventory getPlayerInstancedInventory(PlayerEntity player);
    
    List<Inventory> getAllInstancedInventories();
}
