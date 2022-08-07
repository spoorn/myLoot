package org.spoorn.myloot.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spoorn.myloot.core.LootableContainerReplacer;
import org.spoorn.myloot.util.MyLootUtil;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {

    private static final Logger log = LogManager.getLogger("MyLootBlockEntityMixin");

    @Shadow @Nullable protected World world;

    @Shadow public abstract BlockPos getPos();
    
    @Shadow public abstract BlockState getCachedState();

    /**
     * When BlockEntities are created, add them as a myLoot container if applicable.
     */
    @Inject(method = "setWorld", at = @At(value = "TAIL"))
    private void replaceLootableContainer(World world, CallbackInfo ci) {
        if (this.world instanceof ServerWorld && MyLootUtil.supportedEntity((Object) this)) {
            LootableContainerBlockEntityAccessor accessor = (LootableContainerBlockEntityAccessor) (Object) this;
            if (accessor.getLootTableId() != null) {
                // Skip double chests as they are most likely modded loot chests and we don't (yet) construct a double myLoot chest
                // with the proper items yet.  For example, BetterEnd adds double loot chests in its structures in the Shadow Forest
                // which we could use to test adding this feature later on.
                if ((Object) this instanceof ChestBlockEntity) {
                    BlockState blockState = this.getCachedState();
                    if (blockState != null && blockState.contains(ChestBlock.CHEST_TYPE) && blockState.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE) {
                        log.info("[myLoot] Skipping container {} at {} as it's not a single chest", this, this.getPos());
                        return;
                    }
                }

                LootableContainerReplacer.REPLACEMENT_INFOS.add(new LootableContainerReplacer.ReplacementInfo(this.world.getRegistryKey(), this.getPos(), accessor.getLootTableId(), accessor.getLootTableSeed()));
            }
        }
    }
}
