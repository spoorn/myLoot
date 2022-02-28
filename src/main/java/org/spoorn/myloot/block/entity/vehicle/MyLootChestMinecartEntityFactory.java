package org.spoorn.myloot.block.entity.vehicle;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.world.World;
import org.spoorn.spoornpacks.api.entity.vehicle.SPMinecartEntityFactory;

public class MyLootChestMinecartEntityFactory implements SPMinecartEntityFactory {

    @Override
    public AbstractMinecartEntity.Type getVanillaMinecartEntityType() {
        return AbstractMinecartEntity.Type.CHEST;
    }

    @Override
    public AbstractMinecartEntity create(EntityType<? extends Entity> entityType, World world) {
        return new MyLootChestMinecartEntity((EntityType<? extends ChestMinecartEntity>) entityType, world);
    }

    @Override
    public AbstractMinecartEntity create(World world, double x, double y, double z) {
        return new MyLootChestMinecartEntity(world, x, y, z);
    }
}
