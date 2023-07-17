package org.spoorn.myloot.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spoorn.myloot.block.entity.MyLootContainer;
import org.spoorn.myloot.config.ModConfig;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {

    private static final MutableText ACTIONBAR_BREAK_WARNING = Text.translatable("myloot.breakcontainer.actionbarwarning").setStyle(Style.EMPTY.withColor(Formatting.AQUA));
    private static final MutableText CHAT_BREAK_WARNING = Text.translatable("myloot.breakcontainer.chatwarning").setStyle(Style.EMPTY.withColor(Formatting.RED));
    private static final MutableText DISABLED_ACTIONBAR_BREAK_WARNING = Text.translatable("myloot.breakcontainer.disabledactionbarwarning").setStyle(Style.EMPTY.withColor(Formatting.AQUA));
    private static final MutableText DISABLED_CHAT_BREAK_WARNING = Text.translatable("myloot.breakcontainer.disabledwarning").setStyle(Style.EMPTY.withColor(Formatting.RED));
    private boolean warned = false;

    @Shadow private GameMode gameMode;

    @Shadow @Final private MinecraftClient client;

    @Shadow public abstract void cancelBlockBreaking();

    @Shadow private boolean breakingBlock;

    /**
     * Prevents breaking MyLoot containers unless player is sneaking.  Also displays a message on top of the action bar
     * as well as one time in the chat, warning the player that breaking the my loot container will prevent other players
     * from being able to loot their instanced loot.
     */
    @Inject(method = "updateBlockBreakingProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameMode;isCreative()Z"), cancellable = true)
    private void preventBreakingMyLootContainer(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (!this.gameMode.isCreative()) {
            BlockEntity blockEntity = this.client.world.getBlockEntity(pos);
            if (blockEntity instanceof MyLootContainer) {
                if (shouldCancelBreaking()) {
                    if (this.breakingBlock) {
                        this.cancelBlockBreaking();
                    }
                    cir.setReturnValue(false);
                    cir.cancel();
                }
            }
        }
    }
    
    @Inject(method = "attackEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"), cancellable = true)
    private void preventBreakingMyLootEntities(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (!player.isCreative() && target instanceof MyLootContainer) {
            if (shouldCancelBreaking()) {
                if (this.breakingBlock) {
                    this.cancelBlockBreaking();
                }
                ci.cancel();
            }
        }
    }
    
    private boolean shouldCancelBreaking() {
        boolean cancelBreaking = false;
        if (!ModConfig.get().allowNonCreativeBreak) {
            this.client.player.sendMessage(DISABLED_ACTIONBAR_BREAK_WARNING, true);
            if (!warned) {
                this.client.player.sendMessage(DISABLED_CHAT_BREAK_WARNING, false);
                warned = true;
            }
            cancelBreaking = true;
        } else if (!this.client.player.isSneaking()) {
            this.client.player.sendMessage(ACTIONBAR_BREAK_WARNING, true);
            if (!warned) {
                this.client.player.sendMessage(CHAT_BREAK_WARNING, false);
                warned = true;
            }
            cancelBreaking = true;
        }
        return cancelBreaking;
    }
}
