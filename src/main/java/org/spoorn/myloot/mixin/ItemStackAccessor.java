package org.spoorn.myloot.mixin;

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(ItemStack.class)
public interface ItemStackAccessor {
    
    @Invoker("<init>")
    static ItemStack create(ItemConvertible item, int count, Optional<NbtCompound> nbt) {
        throw new Error("Mixin did not apply!");
    }
}
