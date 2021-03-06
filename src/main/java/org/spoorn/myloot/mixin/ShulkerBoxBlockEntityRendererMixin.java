package org.spoorn.myloot.mixin;

import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spoorn.myloot.MyLoot;
import org.spoorn.myloot.block.entity.MyLootContainer;
import org.spoorn.myloot.util.MyLootUtil;
import org.spoorn.spoornpacks.client.render.SPTexturedRenderLayers;

@Mixin(ShulkerBoxBlockEntityRenderer.class)
public class ShulkerBoxBlockEntityRendererMixin {

    @Shadow @Final private ShulkerEntityModel<?> model;

    @Inject(method = "render(Lnet/minecraft/block/entity/ShulkerBoxBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/SpriteIdentifier;getVertexConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Ljava/util/function/Function;)Lnet/minecraft/client/render/VertexConsumer;"), cancellable = true)
    private void useMyLootShulkerTexture(ShulkerBoxBlockEntity shulkerBoxBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {
        PlayerEntity player = MyLootUtil.getClientPlayerEntity();
        if (shulkerBoxBlockEntity instanceof MyLootContainer myLootContainer) {
            SpriteIdentifier spriteIdentifier;
            if (player != null && myLootContainer.hasPlayerOpened(player)) {
                spriteIdentifier = SPTexturedRenderLayers.getStandardSprite(MyLoot.MODID, "opened_loot", TexturedRenderLayers.SHULKER_BOXES_ATLAS_TEXTURE);
            } else {
                spriteIdentifier = SPTexturedRenderLayers.getStandardSprite(MyLoot.MODID, "loot", TexturedRenderLayers.SHULKER_BOXES_ATLAS_TEXTURE);
            }
            VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntityCutoutNoCull);
            this.model.render(matrixStack, vertexConsumer, i, j, 1.0f, 1.0f, 1.0f, 1.0f);
            matrixStack.pop();
            ci.cancel();
        }
    }
}
