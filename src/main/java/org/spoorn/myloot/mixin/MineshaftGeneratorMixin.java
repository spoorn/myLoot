package org.spoorn.myloot.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.world.StructureWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spoorn.myloot.block.entity.vehicle.MyLootChestMinecartEntity;
import org.spoorn.myloot.util.MyLootUtil;

@Mixin(targets = "net/minecraft/structure/MineshaftGenerator$MineshaftCorridor")
public class MineshaftGeneratorMixin {

    /**
     * Replace Chest Minecarts with myLoot variant in mineshaft generation.
     */
    @Redirect(method = "addChest", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/StructureWorldAccess;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
    private boolean replaceWithMyLootContainer(StructureWorldAccess instance, Entity entity) {
        if (!instance.isClient() && MyLootUtil.supportedEntity(entity)) {
            MyLootChestMinecartEntity myLootChestMinecartEntity = new MyLootChestMinecartEntity(instance.toServerWorld(), entity.getX(), entity.getY(), entity.getZ());
            StorageMinecartEntityAccessor accessor = (StorageMinecartEntityAccessor) entity;
            myLootChestMinecartEntity.setLootTable(accessor.getLootTableId(), accessor.getLootTableSeed());
            instance.spawnEntity(myLootChestMinecartEntity);
            return true;
        } 
        
        return instance.spawnEntity(entity);
    }
}
