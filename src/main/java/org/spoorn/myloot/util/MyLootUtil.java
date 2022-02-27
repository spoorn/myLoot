package org.spoorn.myloot.util;

import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spoorn.myloot.block.entity.MyLootContainerBlockEntity;
import org.spoorn.myloot.config.ModConfig;

import java.util.*;
import java.util.Map.Entry;

public final class MyLootUtil {
    
    public static final String PLAYER_INSTANCE_DROP_BEHAVIOR = "PLAYER_INSTANCE";
    public static final String ALL_DROP_BEHAVIOR = "ALL";
    
    public static boolean supportedBlockEntity(Object be) {
        return (be instanceof ChestBlockEntity) || (be instanceof BarrelBlockEntity);
    }
    
    public static void dropMyLoot(World world, BlockPos pos, Inventory inventory) {
        if (inventory instanceof MyLootContainerBlockEntity myLootContainerBlockEntity) {
            String dropBehavior = ModConfig.get().dropBehavior;
            if (ALL_DROP_BEHAVIOR.equals(dropBehavior)) {
                for (Inventory inv : myLootContainerBlockEntity.getAllInstancedInventories()) {
                    ItemScatterer.spawn(world, pos, inv);
                }
            } else if (PLAYER_INSTANCE_DROP_BEHAVIOR.equals(dropBehavior)) {
                scatterDifferences(world, pos, myLootContainerBlockEntity.getDefaultLoot(), myLootContainerBlockEntity.getOriginalInventory());
            } else {
                throw new RuntimeException("dropBehavior=" + dropBehavior + " is not supported for " + myLootContainerBlockEntity);
            }
        } else {
            ItemScatterer.spawn(world, pos, inventory);
        }
    }
    
    public static DefaultedList<ItemStack> deepCloneInventory(DefaultedList<ItemStack> original) {
        DefaultedList<ItemStack> res = DefaultedList.ofSize(original.size(), ItemStack.EMPTY);
        for (int i = 0; i < original.size(); i++) {
            res.set(i, original.get(i).copy());
        }
        return res;
    }
    
    public static void scatterDifferences(World world, BlockPos pos, DefaultedList<ItemStack> originalLoot, DefaultedList<ItemStack> originalInventory) {
        List<ItemStack> differences = getAdditionsToOriginalLoot(originalLoot, originalInventory);
        for (int i = 0; i < differences.size(); i++) {
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), differences.get(i));
        }
    }
    
    private static List<ItemStack> getAdditionsToOriginalLoot(DefaultedList<ItemStack> originalLoot, DefaultedList<ItemStack> originalInventory) {
        Map<Item, List<ItemStackWrapperWithEqualsAndHashCodeExcludingCount>> originalLootItemsList = constructWrapperMap(originalLoot);
        Map<Item, List<ItemStackWrapperWithEqualsAndHashCodeExcludingCount>> originalInventoryItemsList = constructWrapperMap(originalInventory);
        
        if (originalInventoryItemsList.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<ItemStack> res = new ArrayList<>();
        
        for (Entry<Item, List<ItemStackWrapperWithEqualsAndHashCodeExcludingCount>> entry : originalInventoryItemsList.entrySet()) {
            List<ItemStackWrapperWithEqualsAndHashCodeExcludingCount> originalLootItems = originalLootItemsList.get(entry.getKey());
            List<ItemStackWrapperWithEqualsAndHashCodeExcludingCount> stacks = entry.getValue();
            
            if (originalLootItems == null || originalLootItems.isEmpty()) {
                for (ItemStackWrapperWithEqualsAndHashCodeExcludingCount m : stacks) {
                    res.add(m.stack);
                }
            } else {
                // Assumption at this point is every list in the maps are already compressed
                for (int i = 0; i < stacks.size(); i++) {
                    ItemStackWrapperWithEqualsAndHashCodeExcludingCount curr = stacks.get(i);
                    int indexInOriginalLoot = originalLootItems.indexOf(curr);
                    if (indexInOriginalLoot > -1) {
                        ItemStack origStack = originalLootItems.get(indexInOriginalLoot).stack;
                        int countDiff = curr.stack.getCount() - origStack.getCount();
                        if (countDiff > 0) {
                            ItemStack newStack = origStack.copy();
                            newStack.setCount(countDiff);
                            res.add(newStack);
                        }
                    } else {
                        res.add(curr.stack);
                    }
                }
            }
        }
        
        return res;
    }
    
    // Compressed map of items in an inventory
    private static Map<Item, List<ItemStackWrapperWithEqualsAndHashCodeExcludingCount>> constructWrapperMap(DefaultedList<ItemStack> inventory) {
        Map<Item, List<ItemStackWrapperWithEqualsAndHashCodeExcludingCount>> res = new HashMap<>();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty()) {
                List<ItemStackWrapperWithEqualsAndHashCodeExcludingCount> lst;
                if (res.containsKey(stack.getItem())) {
                    lst = res.get(stack.getItem());
                    ItemStackWrapperWithEqualsAndHashCodeExcludingCount stackWrapper = new ItemStackWrapperWithEqualsAndHashCodeExcludingCount(stack);
                    int indexOf = lst.indexOf(stackWrapper);
                    if (indexOf > -1) {
                        ItemStack s = lst.get(indexOf).stack;
                        s.setCount(s.getCount() + stack.getCount());
                    } else {
                        stackWrapper.cloneAndSetStack();
                        lst.add(stackWrapper);
                    }
                } else {
                    lst = new ArrayList<>();
                    lst.add(new ItemStackWrapperWithEqualsAndHashCodeExcludingCount(stack.copy()));
                    res.put(stack.getItem(), lst);
                }
            }
        }
        return res;
    }
    
    private static class ItemStackWrapperWithEqualsAndHashCodeExcludingCount {
        ItemStack stack;

        ItemStackWrapperWithEqualsAndHashCodeExcludingCount(ItemStack stack) {
            this.stack = stack;
        }
        
        void cloneAndSetStack() {
            this.stack = this.stack.copy();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ItemStackWrapperWithEqualsAndHashCodeExcludingCount other = (ItemStackWrapperWithEqualsAndHashCodeExcludingCount) o;
            ItemStack otherStack = other.stack;
            boolean sameItem = stack.getItem().equals(otherStack.getItem());  // Should always be true
            boolean sameNbtExcludeCount = false;
            if (stack.hasNbt() && otherStack.hasNbt()) {
                NbtCompound nbt1 = stack.getNbt().copy();
                NbtCompound nbt2 = otherStack.getNbt().copy();
                nbt1.remove("Count");
                nbt2.remove("Count");
                sameNbtExcludeCount = nbt1.equals(nbt2);
            }
            boolean sameNbt = (!stack.hasNbt() && !otherStack.hasNbt()) || (sameNbtExcludeCount);
            return sameItem && sameNbt;
        }

        @Override
        public int hashCode() {
            return Objects.hash(stack.getItem(), stack.getNbt());
        }
    }
}
