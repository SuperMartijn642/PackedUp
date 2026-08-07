package com.supermartijn642.packedup.mixin;

import com.supermartijn642.core.gui.WidgetContainerScreen;
import com.supermartijn642.packedup.screen.BackpackContainerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHelper;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created 07/08/2026 by SuperMartijn642
 */
@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Final
    @Shadow
    private MouseHelper mouseHandler;
    @Shadow
    private Screen screen;

    @Unique
    private boolean hadBackpackOpen;
    @Unique
    private double lastMouseX, lastMouseY;

    @Inject(
        method = "tick",
        at = @At("HEAD")
    )
    private void tick(CallbackInfo ci){
        if(!this.hadBackpackOpen)
            return;
        if(this.screen instanceof WidgetContainerScreen<?,?> && ((WidgetContainerScreen<?,?>)this.screen).getWidget() instanceof BackpackContainerScreen){
            this.lastMouseX = this.mouseHandler.xpos();
            this.lastMouseY = this.mouseHandler.ypos();
        }else
            this.hadBackpackOpen = false;
    }

    @Inject(
        method = "setScreen",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/MouseHelper;releaseMouse()V",
            shift = At.Shift.AFTER
        )
    )
    private void preventCursorReset(Screen screen, CallbackInfo ci){
        // Prevent the cursor from being reset to the center of the screen when opening a backpack from a container screen
        if(!(this.screen instanceof WidgetContainerScreen<?,?> && ((WidgetContainerScreen<?,?>)this.screen).getWidget() instanceof BackpackContainerScreen))
            return;
        if(this.hadBackpackOpen)
            //noinspection DataFlowIssue
            GLFW.glfwSetCursorPos(((Minecraft)(Object)this).window.getWindow(), this.lastMouseX, this.lastMouseY);
        this.hadBackpackOpen = true;
    }
}
