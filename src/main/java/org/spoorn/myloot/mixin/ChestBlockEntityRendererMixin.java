package org.spoorn.myloot.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spoorn.myloot.MyLoot;
import org.spoorn.myloot.block.entity.MyLootContainer;
import org.spoorn.spoornpacks.client.render.SPTexturedRenderLayers;

@Mixin(ChestBlockEntityRenderer.class)
public class ChestBlockEntityRendererMixin {

    /**
     * Replace myLoot container texture with the opened variant if player has opened the container.
     */
    @Redirect(method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/TexturedRenderLayers;getChestTexture(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/block/enums/ChestType;Z)Lnet/minecraft/client/util/SpriteIdentifier;"))
    private SpriteIdentifier overrideTextureWithOpened(BlockEntity blockEntity, ChestType type, boolean christmas) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && blockEntity instanceof MyLootContainer myLootContainer) {
            if (myLootContainer.hasPlayerOpened(player)) {
                return SPTexturedRenderLayers.getChest(MyLoot.MODID, "opened_loot", type);
            }
        }
        return TexturedRenderLayers.getChestTexture(blockEntity, type, christmas);
    }
}
