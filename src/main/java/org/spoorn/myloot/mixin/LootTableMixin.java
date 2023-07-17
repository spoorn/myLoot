package org.spoorn.myloot.mixin;

import net.minecraft.inventory.Inventory;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spoorn.myloot.block.entity.MyLootContainer;

@Mixin(LootTable.class)
public class LootTableMixin {
    
    @Inject(method = "supplyInventory", at = @At(value = "TAIL"))
    private void setDefaultInventories(Inventory inventory, LootContextParameterSet parameters, long seed, CallbackInfo ci) {
        if (inventory instanceof MyLootContainer myLootContainer) {
            myLootContainer.setDefaultLoot();
        }
    }
}
