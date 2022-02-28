package org.spoorn.myloot.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spoorn.myloot.block.entity.MyLootContainerBlockEntity;
import org.spoorn.myloot.block.entity.vehicle.MyLootChestMinecartEntity;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

    @Shadow public abstract boolean spawnEntity(Entity entity);

    @Inject(method = "spawnEntity", at = @At(value = "HEAD"), cancellable = true)
    private void replaceWithMyLootEntities(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if ((!(entity instanceof MyLootContainerBlockEntity) && (entity instanceof ChestMinecartEntity))) {
            StorageMinecartEntityAccessor accessor = (StorageMinecartEntityAccessor) entity;
            if (accessor.getLootTableId() != null) {
                MyLootChestMinecartEntity myLootChestMinecartEntity = new MyLootChestMinecartEntity(entity.world, entity.getX(), entity.getY(), entity.getZ());
                myLootChestMinecartEntity.setLootTable(accessor.getLootTableId(), accessor.getLootTableSeed());
                this.spawnEntity(myLootChestMinecartEntity);
                cir.cancel();
            }
        }
    }
}
