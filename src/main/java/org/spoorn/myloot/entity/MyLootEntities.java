package org.spoorn.myloot.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spoorn.myloot.MyLoot;
import org.spoorn.myloot.block.entity.MyLootBarrelBlockEntity;
import org.spoorn.myloot.block.entity.MyLootChestBlockEntity;
import org.spoorn.myloot.block.entity.MyLootShulkerBoxBlockEntity;
import org.spoorn.myloot.block.entity.vehicle.MyLootChestMinecartEntity;

public class MyLootEntities {
    
    public static EntityType<MyLootChestMinecartEntity> MY_LOOT_CHEST_MINECART_ENTITY_TYPE;
    public static BlockEntityType<MyLootChestBlockEntity> MY_LOOT_CHEST_BLOCK_ENTITY_TYPE;
    public static BlockEntityType<MyLootBarrelBlockEntity> MY_LOOT_BARREL_BLOCK_ENTITY_TYPE;
    public static BlockEntityType<MyLootShulkerBoxBlockEntity> MY_LOOT_SHULKER_BOX_BLOCK_ENTITY_TYPE;
    
    public static void init() {
        MY_LOOT_CHEST_MINECART_ENTITY_TYPE = getEntityType("loot_chest_minecart", MyLootChestMinecartEntity.class);
        MY_LOOT_CHEST_BLOCK_ENTITY_TYPE = getBlockEntityType("loot_chest", MyLootChestBlockEntity.class);
        MY_LOOT_BARREL_BLOCK_ENTITY_TYPE = getBlockEntityType("loot_barrel", MyLootBarrelBlockEntity.class);
        MY_LOOT_SHULKER_BOX_BLOCK_ENTITY_TYPE = getBlockEntityType("loot_shulker_box", MyLootShulkerBoxBlockEntity.class);
    }
    
    private static <T extends Entity> EntityType<T> getEntityType(String id, Class<T> clazz) {
        return (EntityType<T>) Registries.ENTITY_TYPE.get(new Identifier(MyLoot.MODID, id));
    }
    
    private static <T extends BlockEntity> BlockEntityType<T> getBlockEntityType(String id, Class<T> clazz) {
        return (BlockEntityType<T>) Registries.BLOCK_ENTITY_TYPE.get(new Identifier(MyLoot.MODID, id));
    }
}
