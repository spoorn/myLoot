package org.spoorn.myloot;

import lombok.extern.log4j.Log4j2;
import net.fabricmc.api.ModInitializer;
import org.spoorn.myloot.block.MyLootBlocks;
import org.spoorn.myloot.core.LootableContainerReplacer;
import org.spoorn.myloot.entity.MyLootEntities;

@Log4j2
public class MyLoot implements ModInitializer {
    
    public static String MODID = "myloot";
    
    @Override
    public void onInitialize() {
        log.info("Hello from myLoot!");
        
        // Blocks
        MyLootBlocks.init();
        
        // Entities
        MyLootEntities.init();

        // Container replacement
        LootableContainerReplacer.init();
    }
}
