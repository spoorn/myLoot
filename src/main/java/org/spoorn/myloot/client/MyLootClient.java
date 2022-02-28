package org.spoorn.myloot.client;

import lombok.extern.log4j.Log4j2;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spoorn.myloot.MyLoot;
import org.spoorn.myloot.client.model.MyLootModelResourceProvider;
import org.spoorn.spoornpacks.client.render.SPTexturedRenderLayers;

@Log4j2
@Environment(EnvType.CLIENT)
public class MyLootClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        log.info("Hello client from myLoot!");

        // Custom TexteredRenderLayers
        //SPTexturedRenderLayers.registerChest(MyLoot.MODID, "opened_loot");
        
        // Barrel custom model
        MyLootModelResourceProvider.init();
    }
}
