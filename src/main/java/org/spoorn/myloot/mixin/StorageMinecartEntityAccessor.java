package org.spoorn.myloot.mixin;

import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StorageMinecartEntity.class)
public interface StorageMinecartEntityAccessor {
    
    @Accessor("inventory")
    DefaultedList<ItemStack> getOriginalStorageInventory();

    @Accessor("lootTableId")
    Identifier getLootTableId();

    @Accessor("lootSeed")
    long getLootTableSeed();
}
