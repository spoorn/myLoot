package org.spoorn.myloot.block.entity;

import lombok.ToString;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

@ToString
public class MyLootInventory implements Inventory {

    private final DefaultedList<ItemStack> inventory;
    // Back reference to the parent ChestBlockEntity.  This causes a circular loop, but gives us access to lots of
    // necessary APIs.
    private final MyLootChestBlockEntity parent;
    
    public MyLootInventory(MyLootChestBlockEntity parent) {
        this(DefaultedList.ofSize(27, ItemStack.EMPTY), parent);
    }
    
    public MyLootInventory(DefaultedList<ItemStack> inventory, MyLootChestBlockEntity parent) {
        this.inventory = inventory;
        this.parent = parent;
    }
    
    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack itemStack = Inventories.splitStack(this.inventory, slot, amount);
        if (!itemStack.isEmpty()) {
            this.markDirty();
        }
        return itemStack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
        this.markDirty();
    }

    @Override
    public void onOpen(PlayerEntity player) {
        this.parent.onOpen(player);
    }

    @Override
    public void onClose(PlayerEntity player) {
        this.parent.onClose(player);
    }

    @Override
    public void markDirty() {
        this.parent.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return this.parent.canPlayerUse(player);
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }
}
