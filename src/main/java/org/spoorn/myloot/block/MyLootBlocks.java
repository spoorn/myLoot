package org.spoorn.myloot.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spoorn.myloot.MyLoot;

public class MyLootBlocks {
    
    public static Block MY_LOOT_CHEST_BLOCK = registerChest("chest");
    
    public static void init() {
        
    }

    public static Block registerChest(String id) {
        // SPChestBlock has a reference to the block name, and actual identifier with _chest suffix
        Block block = new MyLootChestBlock(FabricBlockSettings.copyOf(Blocks.CHEST));
        return registerBlock(id, block);
    }

    private static Block registerBlock(String id, Block block) {
        Identifier identifier = new Identifier(MyLoot.MODID, id);
        return Registry.register(Registry.BLOCK, identifier, block);
    }
}
