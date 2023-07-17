package org.spoorn.myloot;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.log4j.Log4j2;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spoorn.myloot.block.MyLootBarrelBlock;
import org.spoorn.myloot.block.MyLootBlocks;
import org.spoorn.myloot.block.MyLootChestBlock;
import org.spoorn.myloot.block.MyLootShulkerBoxBlock;
import org.spoorn.myloot.block.entity.MyLootBarrelBlockEntity;
import org.spoorn.myloot.block.entity.MyLootChestBlockEntity;
import org.spoorn.myloot.block.entity.MyLootShulkerBoxBlockEntity;
import org.spoorn.myloot.block.entity.vehicle.MyLootChestMinecartEntityFactory;
import org.spoorn.myloot.config.ModConfig;
import org.spoorn.myloot.core.LootableContainerReplacer;
import org.spoorn.myloot.entity.MyLootEntities;
import org.spoorn.myloot.item.MyLootItems;
import org.spoorn.spoornpacks.api.Resource;
import org.spoorn.spoornpacks.api.ResourceBuilder;
import org.spoorn.spoornpacks.api.ResourceFactory;
import org.spoorn.spoornpacks.core.generator.ResourceGenerator;
import org.spoorn.spoornpacks.provider.ResourceProvider;
import org.spoorn.spoornpacks.registry.SpoornPacksRegistry;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.type.ItemType;
import org.spoorn.spoornpacks.type.ResourceType;
import org.spoorn.spoornpacks.type.VehicleType;

@Log4j2
public class MyLoot implements ModInitializer {
    
    public static final String MODID = "myloot";

    private static final RegistryKey<ItemGroup> ITEM_GROUP_REGISTRY_KEY = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(MODID, "general"));
    private static final ItemGroup ITEM_GROUP = Registry.register(Registries.ITEM_GROUP, ITEM_GROUP_REGISTRY_KEY, FabricItemGroup.builder()
            .displayName(Text.translatable("itemGroup.myloot.general"))
            .icon(ResourceFactory.fetchItemGroupSupplierFromBlock(MODID, "loot_chest"))
            .build()
    );
    
    public static final ResourceGenerator RESOURCE_GENERATOR = SpoornPacksRegistry.registerResource(MODID);
    
    private static final class EmptyResourceProvider implements ResourceProvider {

        @Nullable
        @Override
        public ObjectNode getJson() {
            return null;
        }
    }
    
    @Override
    public void onInitialize() {
        log.info("Hello from myLoot!");
        
        // Config
        ModConfig.init();

        String lootName = "loot";
        ResourceBuilder rb = ResourceFactory.create(MyLoot.MODID, lootName, ITEM_GROUP)
                .addBlock(BlockType.CHEST, "loot", new MyLootChestBlock(FabricBlockSettings.copyOf(Blocks.CHEST)), MyLootChestBlockEntity::new)
                .addBlock(BlockType.BARREL, "loot", new MyLootBarrelBlock(FabricBlockSettings.copyOf(Blocks.BARREL)), MyLootBarrelBlockEntity::new)
                .addItem(ItemType.CHEST)
                .addCustomResourceProvider("loot_chest", ResourceType.BLOCK_MODEL,
                        ResourceGenerator.newModelBlockBuilder(MyLoot.MODID, "loot", BlockType.CHEST).chest("minecraft:block/oak_planks"))
                .addCustomResourceProvider("loot_chest", ResourceType.BLOCK_LOOT_TABLE,
                        ResourceGenerator.newBlockLootTableBuilder(MyLoot.MODID, "loot", BlockType.CHEST).chest("minecraft:chest"))
                .addCustomResourceProvider("loot_chest", ResourceType.RECIPE, new EmptyResourceProvider())
                .addCustomResourceProvider("loot_barrel", ResourceType.BLOCK_LOOT_TABLE,
                        ResourceGenerator.newBlockLootTableBuilder(MyLoot.MODID, "loot", BlockType.BARREL).barrel("minecraft:barrel"))
                .addCustomResourceProvider("loot_chest", ResourceType.ITEM_MODEL,
                        ResourceGenerator.newModelItemBuilder(MyLoot.MODID, "loot", ItemType.CHEST).chest("minecraft:block/oak_planks"))
                .addMinecart(VehicleType.CHEST_MINECART, new MyLootChestMinecartEntityFactory())
                .addCustomResourceProvider("loot_chest_minecart",ResourceType.RECIPE,
                        ResourceGenerator.newRecipeBuilder(MyLoot.MODID, "loot", VehicleType.CHEST_MINECART).minecart("myloot:loot_chest", "minecraft:minecart", "myloot:loot_chest_minecart"))
                // For rendering opened Minecarts
                .addItem(ItemType.CHEST, "opened_loot").addBlock(BlockType.CHEST, "opened_loot")
                .addCustomResourceProvider("opened_loot_chest", ResourceType.BLOCK_MODEL,
                        ResourceGenerator.newModelBlockBuilder(MyLoot.MODID, "opened_loot", BlockType.CHEST).chest("minecraft:block/oak_planks"))
                .addCustomResourceProvider("opened_loot_chest", ResourceType.BLOCK_LOOT_TABLE,
                        ResourceGenerator.newBlockLootTableBuilder(MyLoot.MODID, "opened_loot", BlockType.CHEST).chest("minecraft:chest"))
                .addCustomResourceProvider("opened_loot_chest", ResourceType.ITEM_MODEL,
                        ResourceGenerator.newModelItemBuilder(MyLoot.MODID, "opened_loot", ItemType.CHEST).chest("minecraft:block/oak_planks"))
                .addCustomResourceProvider("opened_loot_chest", ResourceType.RECIPE, new EmptyResourceProvider())
                // Shulker Boxes
                .addBlock(BlockType.SHULKER_BOX, "loot", new MyLootShulkerBoxBlock(null, FabricBlockSettings.copyOf(Blocks.SHULKER_BOX)), (pos, state) -> new MyLootShulkerBoxBlockEntity(null, pos, state))
                .addItem(ItemType.SHULKER_BOX)
                .addCustomResourceProvider("loot_shulker_box", ResourceType.RECIPE, new EmptyResourceProvider())
                // Generate resources for opened shulker box for custom rendering
                .addBlock(BlockType.SHULKER_BOX, "opened_loot", new MyLootShulkerBoxBlock(null, FabricBlockSettings.copyOf(Blocks.SHULKER_BOX)), (pos, state) -> new MyLootShulkerBoxBlockEntity(null, pos, state))
                .addCustomResourceProvider("opened_loot_shulker_box", ResourceType.BLOCK_LOOT_TABLE, new EmptyResourceProvider())
                .addCustomResourceProvider("opened_loot_shulker_box", ResourceType.RECIPE, new EmptyResourceProvider());
        Resource resource = MyLoot.RESOURCE_GENERATOR.generate(rb);
        
        // Blocks
        MyLootBlocks.bootstrap(resource);
        
        // Items
        MyLootItems.bootstrap(resource);

        // Entities
        MyLootEntities.init();

        // Container replacement
        LootableContainerReplacer.init();
    }
}
