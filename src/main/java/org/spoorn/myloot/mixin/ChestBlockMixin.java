package org.spoorn.myloot.mixin;

import net.minecraft.block.ChestBlock;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spoorn.myloot.block.entity.MyLootContainerBlockEntity;
import org.spoorn.myloot.util.MyLootUtil;

@Mixin(ChestBlock.class)
public class ChestBlockMixin {
    
    @Redirect(method = "onStateReplaced", at = @At(value = "INVOKE", 
            target = "Lnet/minecraft/util/ItemScatterer;spawn(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/inventory/Inventory;)V"))
    private void noSpawnForMyLoot(World world, BlockPos pos, Inventory inventory) {
        if (inventory instanceof MyLootContainerBlockEntity myLootContainerBlockEntity) {
            MyLootUtil.scatterDifferences(world, pos, myLootContainerBlockEntity.getDefaultLoot(), myLootContainerBlockEntity.getOriginalInventory());
        } else {
            ItemScatterer.spawn(world, pos, inventory);
        }
    }
}
