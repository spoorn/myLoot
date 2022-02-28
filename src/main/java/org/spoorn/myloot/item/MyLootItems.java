package org.spoorn.myloot.item;

import net.minecraft.item.Item;
import org.spoorn.myloot.util.MyLootUtil;
import org.spoorn.spoornpacks.api.Resource;
import org.spoorn.spoornpacks.type.VehicleType;

public class MyLootItems {
    
    public static Item MY_LOOT_CHEST_MINECART;
    
    public static void bootstrap(Resource resource) {
        MY_LOOT_CHEST_MINECART = MyLootUtil.getVehicleItemFromResource(resource, VehicleType.CHEST_MINECART, "loot");
    }
}
