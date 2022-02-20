package org.spoorn.myloot.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spoorn.myloot.MyLoot;
import org.spoorn.myloot.block.MyLootBlocks;
import org.spoorn.myloot.block.entity.MyLootChestBlockEntity;
import org.spoorn.myloot.mixin.BlockEntityRendererFactoriesAccessor;

public class MyLootEntities {
    
    public static final BlockEntityType<MyLootChestBlockEntity> MY_LOOT_CHEST_BLOCK_ENTITY_BLOCK_ENTITY_TYPE = registerChestBlockEntityType(MyLootBlocks.MY_LOOT_CHEST_BLOCK);
    
    public static void init() {
        
    }

    private static BlockEntityType<MyLootChestBlockEntity> registerChestBlockEntityType(Block block) {
        BlockEntityType<MyLootChestBlockEntity> entityType = registerBlockEntity(MyLoot.MODID, "chest",
                FabricBlockEntityTypeBuilder.create(MyLootChestBlockEntity::new, block).build());
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            BlockEntityRendererFactoriesAccessor.register(entityType, ChestBlockEntityRenderer::new);
        }
        return entityType;
    }

    private static <E extends BlockEntity, ET extends BlockEntityType<E>> ET registerBlockEntity(String namespace, String id, ET entityType) {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(namespace, id), entityType);
    }
}
