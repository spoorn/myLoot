package org.spoorn.myloot.mixin;

import net.minecraft.block.BarrelBlock;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spoorn.myloot.util.MyLootUtil;

@Mixin(BarrelBlock.class)
public class BarrelBlockMixin {

    @Redirect(method = "onStateReplaced", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/util/ItemScatterer;spawn(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/inventory/Inventory;)V"))
    private void noSpawnForMyLoot(World world, BlockPos pos, Inventory inventory) {
        MyLootUtil.dropMyLoot(world, pos, inventory);
    }
}
