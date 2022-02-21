package org.spoorn.myloot.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import org.spoorn.myloot.entity.MyLootEntities;

public class MyLootChestBlockEntity extends AbstractMyLootContainerBlockEntity {
    
    public MyLootChestBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(MyLootEntities.MY_LOOT_CHEST_BLOCK_ENTITY_BLOCK_ENTITY_TYPE, blockPos, blockState);
    }
    
    @Override
    protected Text getContainerName() {
        return new TranslatableText("myloot.loot_chest.container.name");
    }
}
