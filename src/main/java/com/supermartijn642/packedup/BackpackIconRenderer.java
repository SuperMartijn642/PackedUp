package com.supermartijn642.packedup;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.supermartijn642.core.ClientUtils;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Set;

/**
 * Created 15/01/2025 by SuperMartijn642
 */
public class BackpackIconRenderer implements ItemModel.Unbaked {

    public static final MapCodec<BackpackIconRenderer> CODEC = MapCodec.unit(new BackpackIconRenderer());
    private static final SpecialModelRenderer<ItemStack> ICON_RENDERER = new SpecialModelRenderer<>() {
        private static final ThreadLocal<Boolean> RECURSION_GUARD = ThreadLocal.withInitial(() -> false);

        @Override
        public void submit(ItemStack icon, ItemDisplayContext displayContext, PoseStack poseStack, SubmitNodeCollector output, int combinedLight, int combinedOverlay, boolean hasFoil, int k){
            // Render the icon
            if(RECURSION_GUARD.get() == Boolean.TRUE)
                return;
            RECURSION_GUARD.set(true);
            poseStack.pushPose();
            poseStack.translate(0.5f, 0.4f, 1);
            poseStack.scale(0.7f, 0.7f, 0.7f);
            TrackingItemStackRenderState trackingItemStackRenderState = new TrackingItemStackRenderState();
            ClientUtils.getMinecraft().getItemModelResolver().updateForTopItem(trackingItemStackRenderState, icon, ItemDisplayContext.GUI, null, null, k);
            trackingItemStackRenderState.submit(poseStack, output, combinedLight, combinedOverlay, k);
            poseStack.popPose();
            RECURSION_GUARD.remove();
        }

        @Override
        public void getExtents(Set<Vector3f> set){
        }

        @Override
        public @Nullable ItemStack extractArgument(ItemStack stack){
            return null;
        }
    };

    @Override
    public MapCodec<? extends ItemModel.Unbaked> type(){
        return CODEC;
    }

    @Override
    public ItemModel bake(ItemModel.BakingContext context){
        return (renderState, stack, modelResolver, transformType, level, entity, someRandomId) -> {
            renderState.appendModelIdentityElement(this);
            if(transformType != ItemDisplayContext.GUI)
                return;
            // Get the icon
            ItemStack icon = BackpackItem.getIcon(stack);
            // Add the renderer for the icon
            if(!icon.isEmpty()){
                renderState.newLayer().setupSpecialModel(ICON_RENDERER, icon);
                renderState.appendModelIdentityElement(icon.getItem());
            }
        };
    }

    @Override
    public void resolveDependencies(Resolver resolver){
    }
}
