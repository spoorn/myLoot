package org.spoorn.myloot.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spoorn.myloot.block.entity.MyLootContainer;
import org.spoorn.myloot.config.ModConfig;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {

    @Shadow protected ServerWorld world;

    @Shadow @Final protected ServerPlayerEntity player;

    @Shadow protected abstract void method_41250(BlockPos pos, boolean success, int sequence, String reason);

    @Inject(method = "tryBreakBlock", at = @At(value = "HEAD"), cancellable = true)
    private void preventBreakingMyLootContainerInTryBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockEntity be = this.world.getBlockEntity(pos);
        if (be instanceof MyLootContainer && !this.player.isCreativeLevelTwoOp()) {
            if (shouldCancelBreaking()) {
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }

    @Inject(method = "processBlockBreakingAction", at = @At(value = "HEAD"), cancellable = true)
    private void preventBreakingMyLootContainerInBlockBreaking(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight, int sequence, CallbackInfo ci) {
        BlockEntity be = this.world.getBlockEntity(pos);
        if (be instanceof MyLootContainer && !this.player.isCreativeLevelTwoOp()) {
            if (shouldCancelBreaking()) {
                this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(pos, this.world.getBlockState(pos)));
                this.method_41250(pos, false, sequence, "breaking myLoot containers is disabled in the config");
                ci.cancel();
            }
        }
    }

    private boolean shouldCancelBreaking() {
        return !ModConfig.get().allowNonCreativeBreak || !this.player.isSneaking();
    }
}
