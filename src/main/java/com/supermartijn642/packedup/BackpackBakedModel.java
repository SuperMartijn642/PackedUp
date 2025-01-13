package com.supermartijn642.packedup;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraftforge.client.model.data.IModelData;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;
import java.util.Random;

/**
 * Created 15/01/2025 by SuperMartijn642
 */
public class BackpackBakedModel implements IBakedModel {

    private final IBakedModel original;
    private final IBakedModel guiModel;

    private BackpackBakedModel(IBakedModel original, boolean isGuiModel){
        this.original = original;
        this.guiModel = isGuiModel ? null : new BackpackBakedModel(original, true){
            @Override
            public boolean isCustomRenderer(){
                return true;
            }
        };
    }

    public BackpackBakedModel(IBakedModel original){
        this(original, false);
    }

    @Override
    public Pair<? extends IBakedModel,Matrix4f> handlePerspective(ItemCameraTransforms.TransformType transformType){
        Pair<? extends IBakedModel,Matrix4f> pair = this.original.handlePerspective(transformType);
        if(transformType == ItemCameraTransforms.TransformType.GUI && this.guiModel != null)
            pair = Pair.of(this.guiModel, pair.getRight());
        return pair;
    }

    @Override
    public boolean isCustomRenderer(){
        return false;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, Random random){
        return this.original.getQuads(state, direction, random);
    }

    @Override
    public boolean useAmbientOcclusion(){
        return this.original.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d(){
        return this.original.isGui3d();
    }

    @Override
    public TextureAtlasSprite getParticleIcon(){
        return this.original.getParticleIcon();
    }

    @Override
    public ItemCameraTransforms getTransforms(){
        return this.original.getTransforms();
    }

    @Override
    public ItemOverrideList getOverrides(){
        return this.original.getOverrides();
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData){
        return this.original.getQuads(state, side, rand, extraData);
    }

    @Override
    public boolean isAmbientOcclusion(BlockState state){
        return this.original.isAmbientOcclusion(state);
    }

    @Override
    public boolean doesHandlePerspectives(){
        return this.original.doesHandlePerspectives();
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull IEnviromentBlockReader level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData){
        return this.original.getModelData(level, pos, state, tileData);
    }

    @Override
    public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data){
        return this.original.getParticleTexture(data);
    }
}
