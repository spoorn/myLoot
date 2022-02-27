package org.spoorn.myloot;

import lombok.extern.log4j.Log4j2;
import net.fabricmc.api.ModInitializer;
import org.spoorn.myloot.block.MyLootBlocks;
import org.spoorn.myloot.config.ModConfig;
import org.spoorn.myloot.core.LootableContainerReplacer;
import org.spoorn.myloot.entity.MyLootEntities;
import org.spoorn.spoornpacks.client.render.SPTexturedRenderLayers;
import org.spoorn.spoornpacks.core.generator.ResourceGenerator;
import org.spoorn.spoornpacks.registry.SpoornPacksRegistry;

@Log4j2
public class MyLoot implements ModInitializer {
    
    public static final String MODID = "myloot";
    public static ResourceGenerator RESOURCE_GENERATOR = SpoornPacksRegistry.registerResource(MODID);
    
    @Override
    public void onInitialize() {
        log.info("Hello from myLoot!");
        
        // Config
        ModConfig.init();
        
        // Blocks
        MyLootBlocks.init();

        // Entities
        MyLootEntities.init();

        // Container replacement
        LootableContainerReplacer.init();
    }
}
