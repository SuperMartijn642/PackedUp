package com.supermartijn642.packedup;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.render.CustomItemRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Created 13/01/2025 by SuperMartijn642
 */
public class BackpackItemRenderer implements CustomItemRenderer {

    private static final ThreadLocal<Boolean> RECURSION_GUARD = ThreadLocal.withInitial(() -> false);

    @Override
    public void render(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay){
        // Render the regular model
        renderItemModel(stack, poseStack, bufferSource, combinedLight, combinedOverlay);
        // Render the icon
        if(transformType == ItemDisplayContext.GUI && !RECURSION_GUARD.get()){
            RECURSION_GUARD.set(true);
            ItemStack icon = BackpackItem.getIcon(stack);
            if(!icon.isEmpty()){
                poseStack.pushPose();
                poseStack.translate(0.5f, 0.4f, 1);
                poseStack.scale(0.7f, 0.7f, 0.7f);
                ClientUtils.getItemRenderer().renderStatic(icon, ItemDisplayContext.GUI, combinedLight, combinedOverlay, poseStack, bufferSource, null, 0);
                poseStack.popPose();
            }
            RECURSION_GUARD.remove();
        }
    }

    private static void renderItemModel(ItemStack stack, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay){
        ItemRenderer renderer = ClientUtils.getItemRenderer();
        BakedModel model = renderer.getModel(stack, null, null, 0);
        RenderType renderType = ItemBlockRenderTypes.getRenderType(stack, true);
        VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(bufferSource, renderType, true, stack.hasFoil());
        renderer.renderModelLists(model, stack, combinedLight, combinedOverlay, poseStack, vertexConsumer);
    }
}
