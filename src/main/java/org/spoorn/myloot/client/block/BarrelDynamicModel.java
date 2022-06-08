package org.spoorn.myloot.client.block;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;
import org.spoorn.myloot.block.entity.MyLootBarrelBlockEntity;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Dynamic Barrel model that changes barrel model if player has opened myLoot barrel.
 */
public class BarrelDynamicModel implements UnbakedModel {
    
    private final UnbakedModel unopened;
    private final UnbakedModel opened;
    
    public BarrelDynamicModel(UnbakedModel unopened, UnbakedModel opened) {
        this.unopened = unopened;
        this.opened = opened;
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        Collection<Identifier> unopenedIds = this.unopened.getModelDependencies();
        Collection<Identifier> openedIds = this.opened.getModelDependencies();
        return new HashSet<>() {{
            addAll(unopenedIds);
            addAll(openedIds);
        }};
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
        Collection<SpriteIdentifier> unopenedIds = this.unopened.getTextureDependencies(unbakedModelGetter, unresolvedTextureReferences);
        Collection<SpriteIdentifier> openedIds = this.opened.getTextureDependencies(unbakedModelGetter, unresolvedTextureReferences);
        return new HashSet<>() {{
            addAll(unopenedIds);
            addAll(openedIds);
        }};
    }

    @Nullable
    @Override
    public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
        return new BarrelBakedModel(
                bakedModel(this.unopened, loader, textureGetter, rotationContainer, modelId),
                bakedModel(this.opened, loader, textureGetter, rotationContainer, modelId));
    }
    
    private BakedModel bakedModel(UnbakedModel unbakedModel, ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
        return unbakedModel.bake(loader, textureGetter, rotationContainer, modelId);
    }
    
    private static final class BarrelBakedModel implements BakedModel, FabricBakedModel {

        private final BakedModel unopened;
        private final BakedModel opened;
        
        BarrelBakedModel(BakedModel unopened, BakedModel opened) {
            this.unopened = unopened;
            this.opened = opened;
        }

        @Override
        public boolean isVanillaAdapter() {
            return false;
        }

        @Override
        public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
            BlockEntity be = blockView.getBlockEntity(pos);
            BakedModel model = this.unopened;
            if (be instanceof MyLootBarrelBlockEntity myLootBarrelBlockEntity) {
                PlayerEntity player = MinecraftClient.getInstance().player;
                if (player != null && myLootBarrelBlockEntity.hasPlayerOpened(player)) {
                    model = this.opened;
                }
            }
            
            QuadEmitter emitter = context.getEmitter();
            Renderer renderer = RendererAccess.INSTANCE.getRenderer();
            if (renderer != null) {
                RenderMaterial material = renderer.materialById(RenderMaterial.MATERIAL_STANDARD);
                for (Direction dir : Direction.values()) {
                    for (BakedQuad quad : model.getQuads(state, dir, randomSupplier.get())) {
                        emitter.fromVanilla(quad, material, dir);
                        emitter.emit();
                    }
                }
            }
        }

        @Override
        public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {

        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
            BakedModel model = this.unopened;
            
            ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
            builder.addAll(model.getQuads(state, face, random));
            return builder.build();
        }

        @Override
        public boolean useAmbientOcclusion() {
            return true;
        }

        @Override
        public boolean hasDepth() {
            return true;
        }

        @Override
        public boolean isSideLit() {
            return false;
        }

        @Override
        public boolean isBuiltin() {
            return false;
        }

        @Override
        public Sprite getParticleSprite() {
            return this.unopened.getParticleSprite();
        }

        @Override
        public ModelTransformation getTransformation() {
            return null;
        }

        @Override
        public ModelOverrideList getOverrides() {
            return null;
        }
    }
}
