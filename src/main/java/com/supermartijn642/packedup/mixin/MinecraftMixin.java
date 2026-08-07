package com.supermartijn642.packedup.mixin;

import com.supermartijn642.core.gui.WidgetContainerScreen;
import com.supermartijn642.packedup.screen.BackpackContainerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MouseHelper;
import org.lwjgl.input.Mouse;
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
    private MouseHelper mouseHelper;
    @Shadow
    private GuiScreen currentScreen;

    @Unique
    private boolean hadBackpackOpen;
    @Unique
    private int lastMouseX, lastMouseY;

    @Inject(
        method = "runTick",
        at = @At("HEAD")
    )
    private void runTick(CallbackInfo ci){
        if(!this.hadBackpackOpen)
            return;
        if(this.currentScreen instanceof WidgetContainerScreen<?,?> && ((WidgetContainerScreen<?,?>)this.currentScreen).getWidget() instanceof BackpackContainerScreen){
            this.lastMouseX = this.mouseHelper.deltaX;
            this.lastMouseY = this.mouseHelper.deltaY;
        }else
            this.hadBackpackOpen = false;
    }

    @Inject(
        method = "displayGuiScreen",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Minecraft;setIngameNotInFocus()V",
            shift = At.Shift.AFTER
        )
    )
    private void preventCursorReset(GuiScreen screen, CallbackInfo ci){
        // Prevent the cursor from being reset to the center of the screen when opening a backpack from a container screen
        if(!(this.currentScreen instanceof WidgetContainerScreen<?,?> && ((WidgetContainerScreen<?,?>)this.currentScreen).getWidget() instanceof BackpackContainerScreen))
            return;
        if(this.hadBackpackOpen)
            Mouse.setCursorPosition(this.lastMouseX, this.lastMouseY);
        this.hadBackpackOpen = true;
    }
}
