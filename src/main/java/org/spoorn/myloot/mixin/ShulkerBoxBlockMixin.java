package org.spoorn.myloot.mixin;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spoorn.myloot.block.MyLootBlocks;
import org.spoorn.myloot.block.MyLootShulkerBoxBlock;
import org.spoorn.myloot.block.entity.MyLootContainer;
import org.spoorn.myloot.block.entity.common.MyLootContainerBlockEntityCommon;
import org.spoorn.myloot.util.MyLootUtil;

import java.util.List;

@Mixin(ShulkerBoxBlock.class)
public class ShulkerBoxBlockMixin {
    
    @Redirect(method = "appendTooltip", at = @At(value = "INVOKE", 
            target = "Lnet/minecraft/inventory/Inventories;readNbt(Lnet/minecraft/nbt/NbtCompound;Lnet/minecraft/util/collection/DefaultedList;)V"))
    private void usePlayerInstancedInventory(NbtCompound nbt, DefaultedList<ItemStack> stacks, ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        if (world != null) {
            PlayerEntity player = MyLootUtil.getClientPlayerEntity();
            if (player != null && nbt.contains(MyLootContainerBlockEntityCommon.NBT_KEY)) {
                String playerId = player.getGameProfile().getId().toString();
                if (playerId != null) {
                    NbtCompound sub = nbt.getCompound(MyLootContainerBlockEntityCommon.NBT_KEY);
                    Inventories.readNbt(sub.getCompound(playerId), stacks);
                    return;
                }
            }
        }
        Inventories.readNbt(nbt, stacks);
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
        if (instance instanceof MyLootContainer myLootContainer) {
            for (Inventory inventory : myLootContainer.getAllInstancedInventories()) {
                if (!inventory.isEmpty()) {
                    return false;
                }
            }
        }
        return instance.isEmpty();
    }
}
