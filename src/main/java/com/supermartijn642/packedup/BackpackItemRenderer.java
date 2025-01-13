package com.supermartijn642.packedup;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.render.CustomItemRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;

/**
 * Created 13/01/2025 by SuperMartijn642
 */
public class BackpackItemRenderer implements CustomItemRenderer {

    private static final ThreadLocal<Boolean> RECURSION_GUARD = ThreadLocal.withInitial(() -> false);

    @Override
    public void render(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int combinedLight, int combinedOverlay){
        // Render the regular model
        renderItemModel(stack, poseStack, bufferSource, combinedLight, combinedOverlay);
        // Render the icon
        if(transformType == ItemCameraTransforms.TransformType.GUI && !RECURSION_GUARD.get()){
            RECURSION_GUARD.set(true);
            ItemStack icon = BackpackItem.getIcon(stack);
            if(!icon.isEmpty()){
                poseStack.pushPose();
                poseStack.translate(0.5f, 0.4f, 1);
                poseStack.scale(0.7f, 0.7f, 0.7f);
                ClientUtils.getItemRenderer().renderStatic(icon, ItemCameraTransforms.TransformType.GUI, combinedLight, combinedOverlay, poseStack, bufferSource);
                poseStack.popPose();
            }
            RECURSION_GUARD.remove();
        }
    }

    private static void renderItemModel(ItemStack stack, MatrixStack poseStack, IRenderTypeBuffer bufferSource, int combinedLight, int combinedOverlay){
        ItemRenderer renderer = ClientUtils.getItemRenderer();
        IBakedModel model = renderer.getModel(stack, null, null);
        RenderType renderType = RenderTypeLookup.getRenderType(stack);
        IVertexBuilder vertexConsumer = ItemRenderer.getFoilBuffer(bufferSource, renderType, true, stack.hasFoil());
        renderer.renderModelLists(model, stack, combinedLight, combinedOverlay, poseStack, vertexConsumer);
    }
}
