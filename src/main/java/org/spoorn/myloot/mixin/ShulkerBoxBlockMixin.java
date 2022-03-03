package org.spoorn.myloot.mixin;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spoorn.myloot.block.MyLootBlocks;
import org.spoorn.myloot.block.MyLootShulkerBoxBlock;
import org.spoorn.myloot.block.entity.MyLootContainerBlockEntity;

@Mixin(ShulkerBoxBlock.class)
public class ShulkerBoxBlockMixin {
    
    @Redirect(method = "appendTooltip", at = @At(value = "INVOKE", 
            target = "Lnet/minecraft/inventory/Inventories;readNbt(Lnet/minecraft/nbt/NbtCompound;Lnet/minecraft/util/collection/DefaultedList;)V"))
    private void usePlayerInstancedInventory(NbtCompound nbt, DefaultedList<ItemStack> stacks) {
        
    }

    @Redirect(method = "onBreak", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/ShulkerBoxBlock;getItemStack(Lnet/minecraft/util/DyeColor;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack dropMyLootItemStack(DyeColor color) {
        if (((Object) this) instanceof MyLootShulkerBoxBlock) {
            return new ItemStack(MyLootBlocks.MY_LOOT_SHULKER_BOX_BLOCK);
        }
        return ShulkerBoxBlock.getItemStack(color);
    }
    
    @Redirect(method = "onBreak", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/ShulkerBoxBlockEntity;isEmpty()Z"))
    private boolean alsoCheckPlayerInstancedInventories(ShulkerBoxBlockEntity instance) {
        if (instance instanceof MyLootContainerBlockEntity myLootContainerBlockEntity) {
            for (Inventory inventory : myLootContainerBlockEntity.getAllInstancedInventories()) {
                if (!inventory.isEmpty()) {
                    return false;
                }
            }
        }
        return instance.isEmpty();
    }
}
