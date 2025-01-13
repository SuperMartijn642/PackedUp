package com.supermartijn642.packedup;

import com.mojang.blaze3d.platform.GlStateManager;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.render.CustomItemRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;

/**
 * Created 13/01/2025 by SuperMartijn642
 */
public class BackpackItemRenderer implements CustomItemRenderer {

    private static final ThreadLocal<Boolean> RECURSION_GUARD = ThreadLocal.withInitial(() -> false);

    @Override
    public void render(ItemStack stack){
        // Render the regular model
        renderItemModel(stack);
        // Render the icon
        if(!RECURSION_GUARD.get()){
            RECURSION_GUARD.set(true);
            ItemStack icon = BackpackItem.getIcon(stack);
            if(!icon.isEmpty()){
                GlStateManager.pushMatrix();
                GlStateManager.translatef(0.5f, 0.4f, 1);
                GlStateManager.scalef(0.7f, 0.7f, 0.7f);
                ClientUtils.getItemRenderer().renderStatic(icon, ItemCameraTransforms.TransformType.GUI);
                GlStateManager.popMatrix();
            }
            RECURSION_GUARD.remove();
        }
    }

    private static void renderItemModel(ItemStack stack){
        ItemRenderer renderer = ClientUtils.getItemRenderer();
        IBakedModel model = renderer.getModel(stack);
        renderer.renderModelLists(model, -1, stack);
        if(stack.hasFoil())
            ItemRenderer.renderFoilLayer(ClientUtils.getTextureManager(), () -> renderer.renderModelLists(model, -8372020, ItemStack.EMPTY), 8);
    }
}
