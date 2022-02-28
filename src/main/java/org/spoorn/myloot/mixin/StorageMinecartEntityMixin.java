package org.spoorn.myloot.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spoorn.myloot.util.MyLootUtil;

@Mixin(StorageMinecartEntity.class)
public class StorageMinecartEntityMixin {
    
    @Redirect(method = "dropItems", at = @At(value = "INVOKE", 
            target = "Lnet/minecraft/util/ItemScatterer;spawn(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;Lnet/minecraft/inventory/Inventory;)V"))
    private void handleSpawnForMyLoot(World world, Entity entity, Inventory inventory) {
        MyLootUtil.dropMyLoot(world, entity.getBlockPos(), inventory);
    }
}
