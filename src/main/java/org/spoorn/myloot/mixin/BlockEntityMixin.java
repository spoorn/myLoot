package org.spoorn.myloot.mixin;

import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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

    @Shadow @Nullable protected World world;

    @Shadow public abstract BlockPos getPos();

    /**
     * When BlockEntities are created, add them as a myLoot container if applicable.
     */
    @Inject(method = "setWorld", at = @At(value = "TAIL"))
    private void replaceLootableContainer(World world, CallbackInfo ci) {
        if (this.world instanceof ServerWorld && MyLootUtil.supportedEntity((Object) this)) {
            LootableContainerBlockEntityAccessor accessor = (LootableContainerBlockEntityAccessor) (Object) this;
            if (accessor.getLootTableId() != null) {
                LootableContainerReplacer.REPLACEMENT_INFOS.add(new LootableContainerReplacer.ReplacementInfo(this.world.getRegistryKey(), this.getPos(), accessor.getLootTableId(), accessor.getLootTableSeed()));
            }
        }
    }
}
