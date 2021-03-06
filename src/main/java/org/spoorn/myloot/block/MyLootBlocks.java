package org.spoorn.myloot.block;

import net.minecraft.block.Block;
import org.spoorn.myloot.util.MyLootUtil;
import org.spoorn.spoornpacks.api.Resource;
import org.spoorn.spoornpacks.type.BlockType;

public class MyLootBlocks {
    
    public static Block MY_LOOT_CHEST_BLOCK;
    public static Block OPENED_MY_LOOT_CHEST_BLOCK;
    public static Block MY_LOOT_BARREL_BLOCK;
    public static Block MY_LOOT_SHULKER_BOX_BLOCK;
    
    public static void bootstrap(Resource resource) {
        String lootName = "loot";
        MY_LOOT_CHEST_BLOCK = MyLootUtil.getBlockFromResource(resource, BlockType.CHEST, lootName);
        OPENED_MY_LOOT_CHEST_BLOCK = MyLootUtil.getBlockFromResource(resource, BlockType.CHEST, "opened_" + lootName);
        MY_LOOT_BARREL_BLOCK = MyLootUtil.getBlockFromResource(resource, BlockType.BARREL, lootName);
        MY_LOOT_SHULKER_BOX_BLOCK =  MyLootUtil.getBlockFromResource(resource, BlockType.SHULKER_BOX, lootName);
    }
}
