package org.spoorn.myloot.util;

import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;

public final class MyLootUtil {
    
    public static boolean supportedBlockEntity(Object be) {
        return (be instanceof ChestBlockEntity) || (be instanceof BarrelBlockEntity);
    }
}
