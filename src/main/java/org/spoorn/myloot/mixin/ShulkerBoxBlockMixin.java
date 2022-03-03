package org.spoorn.myloot.mixin;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ShulkerBoxBlock.class)
public class ShulkerBoxBlockMixin {
    
    @Redirect(method = "appendTooltip", at = @At(value = "INVOKE", 
            target = "Lnet/minecraft/inventory/Inventories;readNbt(Lnet/minecraft/nbt/NbtCompound;Lnet/minecraft/util/collection/DefaultedList;)V"))
    private void usePlayerInstancedInventory(NbtCompound nbt, DefaultedList<ItemStack> stacks) {
        
    }
}
