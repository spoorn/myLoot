package org.spoorn.myloot.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.world.ChunkRegion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spoorn.myloot.block.entity.MyLootContainerBlockEntity;
import org.spoorn.myloot.block.entity.vehicle.MyLootChestMinecartEntity;

@Mixin(ChunkRegion.class)
public abstract class ChunkRegionMixin {

    @Shadow public abstract boolean spawnEntity(Entity entity);

    @Inject(method = "spawnEntity", at = @At(value = "HEAD"), cancellable = true)
    private void replaceWithMyLootEntities(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if ((!(entity instanceof MyLootContainerBlockEntity) && (entity instanceof ChestMinecartEntity))) {
            MyLootChestMinecartEntity myLootChestMinecartEntity = new MyLootChestMinecartEntity(entity.world, entity.getX(), entity.getY(), entity.getZ());
            StorageMinecartEntityAccessor accessor = (StorageMinecartEntityAccessor) entity;
            myLootChestMinecartEntity.setLootTable(accessor.getLootTableId(), accessor.getLootTableSeed());
            this.spawnEntity(myLootChestMinecartEntity);
            cir.cancel();
        }
    }
}
