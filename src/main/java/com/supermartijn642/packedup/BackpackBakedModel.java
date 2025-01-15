package com.supermartijn642.packedup;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;

/**
 * Created 15/01/2025 by SuperMartijn642
 */
public class BackpackBakedModel implements IBakedModel {

    private final IBakedModel original;
    private final IBakedModel guiModel;

    private BackpackBakedModel(IBakedModel original, boolean isGuiModel){
        this.original = original;
        this.guiModel = isGuiModel ? null : new BackpackBakedModel(original, true) {
            @Override
            public boolean isBuiltInRenderer(){
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
    public boolean isBuiltInRenderer(){
        return false;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing direction, long seed){
        return this.original.getQuads(state, direction, seed);
    }

    @Override
    public boolean isAmbientOcclusion(){
        return this.original.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d(){
        return this.original.isGui3d();
    }

    @Override
    public TextureAtlasSprite getParticleTexture(){
        return this.original.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms(){
        return this.original.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides(){
        return this.original.getOverrides();
    }

    @Override
    public boolean isAmbientOcclusion(IBlockState state){
        return this.original.isAmbientOcclusion(state);
    }
}
