package com.supermartijn642.packedup;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.supermartijn642.core.ClientUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Created 15/01/2025 by SuperMartijn642
 */
public class BackpackIconRenderer implements ItemModel.Unbaked {

    public static final MapCodec<BackpackIconRenderer> CODEC = MapCodec.unit(new BackpackIconRenderer());
    private static final SpecialModelRenderer<ItemStack> ICON_RENDERER = new SpecialModelRenderer<>() {
        private static final ThreadLocal<Boolean> RECURSION_GUARD = ThreadLocal.withInitial(() -> false);

        @Override
        public void render(ItemStack icon, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, boolean hasFoil){
            // Render the icon
            RECURSION_GUARD.set(true);
            poseStack.pushPose();
            poseStack.translate(0.5f, 0.4f, 1);
            poseStack.scale(0.7f, 0.7f, 0.7f);
            ClientUtils.getItemRenderer().renderStatic(icon, ItemDisplayContext.GUI, combinedLight, combinedOverlay, poseStack, bufferSource, null, 0);
            poseStack.popPose();
            RECURSION_GUARD.remove();
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
            if(transformType != ItemDisplayContext.GUI)
                return;
            // Get the icon
            ItemStack icon = BackpackItem.getIcon(stack);
            // Add the renderer for the icon
            if(!icon.isEmpty())
                renderState.newLayer().setupSpecialModel(ICON_RENDERER, icon);
        };
    }

    @Override
    public void resolveDependencies(Resolver resolver){
    }
}
