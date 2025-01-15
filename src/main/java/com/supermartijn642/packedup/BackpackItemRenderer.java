package com.supermartijn642.packedup;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.render.CustomItemRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
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
                GlStateManager.translate(0.5f, 0.4f, 1);
                GlStateManager.scale(0.7f, 0.7f, 0.7f);
                ClientUtils.getItemRenderer().renderItem(icon, ItemCameraTransforms.TransformType.GUI);
                GlStateManager.popMatrix();
            }
            RECURSION_GUARD.remove();
        }
    }

    private static void renderItemModel(ItemStack stack){
        RenderItem renderer = ClientUtils.getItemRenderer();
        IBakedModel model = renderer.getItemModelMesher().getItemModel(stack);
        renderer.renderModel(model, -1, stack);
        if(stack.hasEffect())
            renderer.renderEffect(model);
    }
}
