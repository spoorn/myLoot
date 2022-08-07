package org.spoorn.myloot.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spoorn.myloot.block.entity.MyLootContainer;
import org.spoorn.myloot.core.LootableContainerReplacer;
import org.spoorn.myloot.util.MyLootUtil;

import java.util.Random;

@Mixin(LootableContainerBlockEntity.class)
public class LootableContainerBlockEntityMixin {

    private static final Logger log = LogManager.getLogger("MyLootLootableContainerBlockEntityMixin");
    
    /**
     * Marks lootable containers as replaceable.  The world parameter here can't be used to directly modify blocks as
     * it is a {@link BlockView} which is read-only and will freeze the game.
     */
    @Inject(method = "setLootTable(Lnet/minecraft/world/BlockView;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/Identifier;)V",
        at = @At(value = "HEAD"), cancellable = true)
    private static void replaceLootableContainer(BlockView world, Random random, BlockPos pos, Identifier id, CallbackInfo ci) {
        BlockEntity be = world.getBlockEntity(pos);
        
        if (id == null || be == null || be instanceof MyLootContainer) {
            return;
        }
        
        if (be.getWorld() instanceof ServerWorld && MyLootUtil.supportedEntity(be)) {
            // Skip double chests as they are most likely modded loot chests and we don't (yet) construct a double myLoot chest
            // with the proper items yet.  For example, BetterEnd adds double loot chests in its structures in the Shadow Forest
            // which we could use to test adding this feature later on.
            if (be instanceof ChestBlockEntity) {
                BlockState blockState = be.getCachedState();
                if (blockState != null && blockState.contains(ChestBlock.CHEST_TYPE) && blockState.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE) {
                    log.info("[myLoot] Skipping container {} at {} as it's not a single chest", be, be.getPos());
                    return;
                }
            }
            
            LootableContainerReplacer.REPLACEMENT_INFOS.add(new LootableContainerReplacer.ReplacementInfo(be.getWorld().getRegistryKey(), pos, id, random.nextLong()));
        }
    }
}
