package org.spoorn.myloot.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spoorn.myloot.block.entity.MyLootContainer;
import org.spoorn.myloot.block.entity.vehicle.MyLootChestMinecartEntity;
import org.spoorn.myloot.config.ModConfig;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

    @Shadow public abstract boolean spawnEntity(Entity entity);

    @Inject(method = "spawnEntity", at = @At(value = "HEAD"), cancellable = true)
    private void replaceWithMyLootEntities(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.get().enableChestMinecarts && (!(entity instanceof MyLootContainer) && (entity instanceof ChestMinecartEntity))) {
            StorageMinecartEntityAccessor accessor = (StorageMinecartEntityAccessor) entity;
            Identifier lootTableId = accessor.getLootTableId();
            if (lootTableId != null
                    && !ModConfig.get().disabledDimensions.contains(entity.world.getRegistryKey().getValue().toString())
                    && !ModConfig.get().disabledLootTables.contains(lootTableId.toString())) {
                MyLootChestMinecartEntity myLootChestMinecartEntity = new MyLootChestMinecartEntity(entity.world, entity.getX(), entity.getY(), entity.getZ());
                myLootChestMinecartEntity.setLootTable(lootTableId, accessor.getLootTableSeed());
                this.spawnEntity(myLootChestMinecartEntity);
                cir.cancel();
            }
        }
    }
}
