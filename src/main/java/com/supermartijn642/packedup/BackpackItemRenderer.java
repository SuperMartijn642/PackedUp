package com.supermartijn642.packedup;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.render.CustomItemRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created 13/01/2025 by SuperMartijn642
 */
public class BackpackItemRenderer implements CustomItemRenderer {

    /**
     * {@link RenderItem#renderModel(IBakedModel, int, ItemStack)}
     */
    @SuppressWarnings("JavadocReference")
    private static final Method RENDER_MODEL_METHOD;
    /**
     * {@link RenderItem#renderEffect(IBakedModel)}
     */
    @SuppressWarnings("JavadocReference")
    private static final Method RENDER_EFFECT_METHOD;

    static{
        RENDER_MODEL_METHOD = ObfuscationReflectionHelper.findMethod(RenderItem.class, "func_191967_a", void.class, IBakedModel.class, int.class, ItemStack.class);
        RENDER_EFFECT_METHOD = ObfuscationReflectionHelper.findMethod(RenderItem.class, "func_191966_a", void.class, IBakedModel.class);
        RENDER_MODEL_METHOD.setAccessible(true);
        RENDER_EFFECT_METHOD.setAccessible(true);
    }

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
        try{
            RENDER_MODEL_METHOD.invoke(renderer, model, -1, stack);
            if(stack.hasEffect())
                RENDER_EFFECT_METHOD.invoke(renderer, model);
        }catch(InvocationTargetException | IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }
}
