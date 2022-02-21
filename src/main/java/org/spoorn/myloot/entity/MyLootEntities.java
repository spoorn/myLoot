package org.spoorn.myloot.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spoorn.myloot.MyLoot;
import org.spoorn.myloot.block.entity.MyLootChestBlockEntity;

public class MyLootEntities {
    
    public static BlockEntityType<MyLootChestBlockEntity> MY_LOOT_CHEST_BLOCK_ENTITY_BLOCK_ENTITY_TYPE;
    
    public static void init() {
        MY_LOOT_CHEST_BLOCK_ENTITY_BLOCK_ENTITY_TYPE = getBlockEntityType("loot_chest", MyLootChestBlockEntity.class);
    }
    
    private static <T extends BlockEntity> BlockEntityType<T> getBlockEntityType(String id, Class<T> clazz) {
        return (BlockEntityType<T>) Registry.BLOCK_ENTITY_TYPE.get(new Identifier(MyLoot.MODID, id));
    }

    /*private static BlockEntityType<MyLootChestBlockEntity> registerChestBlockEntityType(Block block) {
        BlockEntityType<MyLootChestBlockEntity> entityType = registerBlockEntity(MyLoot.MODID, "chest",
                FabricBlockEntityTypeBuilder.create(MyLootChestBlockEntity::new, block).build());
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            BlockEntityRendererFactoriesAccessor.register(entityType, ChestBlockEntityRenderer::new);
        }
        return entityType;
    }

    private static <E extends BlockEntity, ET extends BlockEntityType<E>> ET registerBlockEntity(String namespace, String id, ET entityType) {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(namespace, id), entityType);
    }*/
}
