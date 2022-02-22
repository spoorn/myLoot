package org.spoorn.myloot.client.model;

import lombok.extern.log4j.Log4j2;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spoorn.myloot.MyLoot;
import org.spoorn.myloot.client.block.BarrelDynamicModel;

@Log4j2
public class MyLootModelResourceProvider implements ModelResourceProvider {
    
    public static final Identifier MY_LOOT_BARREL_ID = new Identifier(MyLoot.MODID, "block/loot_barrel");
    public static final Identifier MY_LOOT_BARREL_OPEN_ID = new Identifier(MyLoot.MODID, "block/loot_barrel_open");
    public static final Identifier MY_LOOT_UNOPENED_BARREL_ID = new Identifier(MyLoot.MODID, "block/unopened_loot_barrel");
    public static final Identifier MY_LOOT_UNOPENED_BARREL_OPEN_ID = new Identifier(MyLoot.MODID, "block/unopened_loot_barrel_open");
    public static final Identifier MY_LOOT_OPENED_BARREL_ID = new Identifier(MyLoot.MODID, "block/opened_loot_barrel");
    public static final Identifier MY_LOOT_OPENED_BARREL_OPEN_ID = new Identifier(MyLoot.MODID, "block/opened_loot_barrel_open");
    
    public static void init() {
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(m -> new MyLootModelResourceProvider());
    }
    
    @Override
    public @Nullable UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context) throws ModelProviderException {
        try {
            if (MY_LOOT_BARREL_ID.equals(resourceId)) {
                log.info("Loaded custom model for {}", MY_LOOT_BARREL_ID);
                UnbakedModel opened = context.loadModel(MY_LOOT_OPENED_BARREL_ID);
                UnbakedModel unopened = context.loadModel(MY_LOOT_UNOPENED_BARREL_ID);
                return new BarrelDynamicModel(unopened, opened);
            }

            if (MY_LOOT_BARREL_OPEN_ID.equals(resourceId)) {
                log.info("Loaded custom model for {}", MY_LOOT_BARREL_OPEN_ID);
                UnbakedModel opened = context.loadModel(MY_LOOT_OPENED_BARREL_OPEN_ID);
                UnbakedModel unopened = context.loadModel(MY_LOOT_UNOPENED_BARREL_OPEN_ID);
                return new BarrelDynamicModel(unopened, opened);
            }
        } catch (Exception e) {
            log.error("Could not load custom models for MyLoot container=" + resourceId, e);
        }
        return null;
    }
}
