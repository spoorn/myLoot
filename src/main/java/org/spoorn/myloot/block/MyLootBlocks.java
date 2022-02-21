package org.spoorn.myloot.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import org.spoorn.myloot.MyLoot;
import org.spoorn.myloot.block.entity.MyLootChestBlockEntity;
import org.spoorn.spoornpacks.api.Resource;
import org.spoorn.spoornpacks.api.ResourceBuilder;
import org.spoorn.spoornpacks.api.ResourceFactory;
import org.spoorn.spoornpacks.core.generator.ResourceGenerator;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.type.ResourceType;

public class MyLootBlocks {
    
    public static Block MY_LOOT_CHEST_BLOCK;
    
    public static void init() {
        ResourceBuilder rb = ResourceFactory.create(MyLoot.MODID)
                .addBlock(BlockType.CHEST, "loot", new MyLootChestBlock(FabricBlockSettings.copyOf(Blocks.CHEST)), MyLootChestBlockEntity::new)
                .addCustomResourceProvider("loot_chest", ResourceType.BLOCK_MODEL, 
                        ResourceGenerator.newModelBlockBuilder(MyLoot.MODID, "loot", BlockType.CHEST).chest("minecraft:block/oak_planks"))
                .addCustomResourceProvider("loot_chest", ResourceType.BLOCK_LOOT_TABLE,
                        ResourceGenerator.newBlockLootTableBuilder(MyLoot.MODID, "loot", BlockType.CHEST).chest("minecraft:chest"));
        Resource resource = MyLoot.RESOURCE_GENERATOR.generate(rb);
        if (resource.getBlock(BlockType.CHEST, "loot").isEmpty()) {
            throw new RuntimeException("Could not generate myLoot.loot_chest");
        }
        MY_LOOT_CHEST_BLOCK = resource.getBlock(BlockType.CHEST, "loot").get();
    }
}
