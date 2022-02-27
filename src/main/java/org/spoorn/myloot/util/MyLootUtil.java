package org.spoorn.myloot.util;

import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public final class MyLootUtil {
    
    public static boolean supportedBlockEntity(Object be) {
        return (be instanceof ChestBlockEntity) || (be instanceof BarrelBlockEntity);
    }
    
    public static DefaultedList<ItemStack> deepCloneInventory(DefaultedList<ItemStack> original) {
        DefaultedList<ItemStack> res = DefaultedList.ofSize(original.size(), ItemStack.EMPTY);
        for (int i = 0; i < original.size(); i++) {
            res.set(i, original.get(i).copy());
        }
        return res;
    }
}
