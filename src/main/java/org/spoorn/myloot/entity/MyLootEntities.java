package org.spoorn.myloot.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spoorn.myloot.MyLoot;
import org.spoorn.myloot.block.entity.MyLootBarrelBlockEntity;
import org.spoorn.myloot.block.entity.MyLootChestBlockEntity;

public class MyLootEntities {
    
    public static BlockEntityType<MyLootChestBlockEntity> MY_LOOT_CHEST_BLOCK_ENTITY_TYPE;
    public static BlockEntityType<MyLootBarrelBlockEntity> MY_LOOT_BARREL_BLOCK_ENTITY_TYPE;
    
    public static void init() {
        MY_LOOT_CHEST_BLOCK_ENTITY_TYPE = getBlockEntityType("loot_chest", MyLootChestBlockEntity.class);
        MY_LOOT_BARREL_BLOCK_ENTITY_TYPE = getBlockEntityType("loot_barrel", MyLootBarrelBlockEntity.class);
    }
    
    private static <T extends BlockEntity> BlockEntityType<T> getBlockEntityType(String id, Class<T> clazz) {
        return (BlockEntityType<T>) Registry.BLOCK_ENTITY_TYPE.get(new Identifier(MyLoot.MODID, id));
    }
}
