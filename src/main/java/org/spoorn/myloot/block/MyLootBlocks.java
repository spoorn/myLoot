package org.spoorn.myloot.block;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import org.spoorn.myloot.MyLoot;
import org.spoorn.myloot.block.entity.MyLootBarrelBlockEntity;
import org.spoorn.myloot.block.entity.MyLootChestBlockEntity;
import org.spoorn.myloot.block.entity.vehicle.MyLootChestMinecartEntity;
import org.spoorn.myloot.block.entity.vehicle.MyLootChestMinecartEntityFactory;
import org.spoorn.spoornpacks.api.Resource;
import org.spoorn.spoornpacks.api.ResourceBuilder;
import org.spoorn.spoornpacks.api.ResourceFactory;
import org.spoorn.spoornpacks.core.generator.ResourceGenerator;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.type.ItemType;
import org.spoorn.spoornpacks.type.ResourceType;
import org.spoorn.spoornpacks.type.VehicleType;

public class MyLootBlocks {
    
    public static Block MY_LOOT_CHEST_BLOCK;
    public static Block MY_LOOT_BARREL_BLOCK;

    private static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
            new Identifier(MyLoot.MODID, "general"),
            ResourceFactory.fetchItemGroupSupplierFromBlock(MyLoot.MODID, "loot_chest")
    );
    
    public static void init() {
        String lootName = "loot";
        ResourceBuilder rb = ResourceFactory.create(MyLoot.MODID, lootName, ITEM_GROUP)
                .addBlock(BlockType.CHEST, "loot", new MyLootChestBlock(FabricBlockSettings.copyOf(Blocks.CHEST)), MyLootChestBlockEntity::new)
                .addBlock(BlockType.BARREL, "loot", new MyLootBarrelBlock(FabricBlockSettings.copyOf(Blocks.BARREL)), MyLootBarrelBlockEntity::new)
                .addItem(ItemType.CHEST)
                .addCustomResourceProvider("loot_chest", ResourceType.BLOCK_MODEL, 
                        ResourceGenerator.newModelBlockBuilder(MyLoot.MODID, "loot", BlockType.CHEST).chest("minecraft:block/oak_planks"))
                .addCustomResourceProvider("loot_chest", ResourceType.BLOCK_LOOT_TABLE,
                        ResourceGenerator.newBlockLootTableBuilder(MyLoot.MODID, "loot", BlockType.CHEST).chest("minecraft:chest"))
                .addCustomResourceProvider("loot_barrel", ResourceType.BLOCK_LOOT_TABLE,
                        ResourceGenerator.newBlockLootTableBuilder(MyLoot.MODID, "loot", BlockType.BARREL).barrel("minecraft:barrel"))
                .addCustomResourceProvider("loot_chest", ResourceType.ITEM_MODEL,
                        ResourceGenerator.newModelItemBuilder(MyLoot.MODID, "loot", ItemType.CHEST).chest("minecraft:block/oak_planks"))
                .addMinecart(VehicleType.CHEST_MINECART, new MyLootChestMinecartEntityFactory())
                .addCustomResourceProvider("loot_chest_minecart",ResourceType.RECIPE,
                        ResourceGenerator.newRecipeBuilder(MyLoot.MODID, "loot", VehicleType.CHEST_MINECART).minecart("myloot:loot_chest", "minecraft:minecart", "myloot:loot_chest_minecart"));
        Resource resource = MyLoot.RESOURCE_GENERATOR.generate(rb);
        if (resource.getBlock(BlockType.CHEST, lootName).isEmpty()) {
            throw new RuntimeException("Could not generate block myLoot.loot_chest");
        }
        if (resource.getBlock(BlockType.BARREL, lootName).isEmpty()) {
            throw new RuntimeException("Could not generate block myLoot.loot_barrel");
        }
        MY_LOOT_CHEST_BLOCK = resource.getBlock(BlockType.CHEST, lootName).get();
        MY_LOOT_BARREL_BLOCK = resource.getBlock(BlockType.BARREL, lootName).get();

        if (resource.getItem(ItemType.CHEST, lootName).isEmpty()) {
            throw new RuntimeException("Could not generate item myLoot.loot_chest");
        }

        if (resource.getVehicleItem(VehicleType.CHEST_MINECART, lootName).isEmpty()) {
            throw new RuntimeException("Could not generate item myLoot.loot_chest");
        }
    }
}
