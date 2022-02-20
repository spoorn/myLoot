package org.spoorn.myloot.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spoorn.myloot.block.entity.MyLootChestBlockEntity;
import org.spoorn.myloot.core.LootableContainerReplacer;

import java.util.Random;

@Mixin(LootableContainerBlockEntity.class)
public class LootableContainerBlockEntityMixin {
    
    private static final Logger log = LogManager.getLogger("LootableContainerBlockEntityMixin");

    /**
     * Marks lootable containers as replaceable.  The world parameter here can't be used to directly modify blocks as
     * it is a {@link BlockView} which is read-only and will freeze the game.
     */
    @Inject(method = "setLootTable(Lnet/minecraft/world/BlockView;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/Identifier;)V",
        at = @At(value = "HEAD"), cancellable = true)
    private static void replaceLootableContainer(BlockView world, Random random, BlockPos pos, Identifier id, CallbackInfo ci) {
        log.info("replaceLootableContainer");
        BlockEntity be = world.getBlockEntity(pos);
        
        log.info("BlockEntity: " + be);
        if (be instanceof MyLootChestBlockEntity) {
            return;
        }
        
        if (be instanceof ChestBlockEntity) {
            LootableContainerReplacer.REPLACEMENT_INFOS.add(new LootableContainerReplacer.ReplacementInfo(pos, id, random.nextLong()));
        }
    }
}
