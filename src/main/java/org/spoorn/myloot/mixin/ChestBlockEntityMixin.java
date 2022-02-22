package org.spoorn.myloot.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spoorn.myloot.block.entity.MyLootChestBlockEntity;
import org.spoorn.myloot.block.entity.MyLootInventory;

import java.util.Map;

@Mixin(ChestBlockEntity.class)
public class ChestBlockEntityMixin {
    
    @Inject(method = "copyInventory", at = @At(value = "TAIL"))
    private static void copyMyLootInventories(ChestBlockEntity from, ChestBlockEntity to, CallbackInfo ci) {
        if (from instanceof MyLootChestBlockEntity fromMyLootContainer && to instanceof MyLootChestBlockEntity toMyLootContainer) {
            Map<String, MyLootInventory> inventories = fromMyLootContainer.getInventories();
            fromMyLootContainer.setInventories(toMyLootContainer.getInventories());
            toMyLootContainer.setInventories(inventories);
        }
    }
    
    @Inject(method = "getPlayersLookingInChestCount", at = @At(value = "TAIL"))
    private static void getPlayersLookingInMyLootChestCount(BlockView world, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.hasBlockEntity() && world.getBlockEntity(pos) instanceof MyLootChestBlockEntity myLootContainerBlockEntity) {
            cir.setReturnValue(myLootContainerBlockEntity.stateManager.getViewerCount());
        }
    }
}
