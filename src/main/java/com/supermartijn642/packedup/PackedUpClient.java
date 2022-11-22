package com.supermartijn642.packedup;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.WidgetContainerScreen;
import com.supermartijn642.core.gui.WidgetScreen;
import com.supermartijn642.core.registry.ClientRegistrationHandler;
import com.supermartijn642.packedup.packets.PacketOpenBag;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
@Mod.EventBusSubscriber(Side.CLIENT)
public class PackedUpClient {

    private static KeyBinding OPEN_BAG_KEY;

    public static void register(){
        ClientRegistrationHandler handler = ClientRegistrationHandler.get("packedup");
        // Register screen for the backpack inventory
        handler.registerContainerScreen(() -> PackedUp.container, container -> WidgetContainerScreen.of(new BackpackContainerScreen(), container, true));

        // Register key to open backpack in inventory or curious slot
        OPEN_BAG_KEY = new KeyBinding("packedup.keys.openbag", 24/*'o'*/, "packedup.keys.category");
        ClientRegistry.registerKeyBinding(OPEN_BAG_KEY);
    }

    public static void openBackpackRenameScreen(String defaultName, String name){
        ClientUtils.displayScreen(WidgetScreen.of(new BackpackRenameScreen(defaultName, name)));
    }

    @SubscribeEvent
    public static void onKey(InputEvent.KeyInputEvent e){
        if(OPEN_BAG_KEY != null && OPEN_BAG_KEY.isPressed() && ClientUtils.getWorld() != null && ClientUtils.getMinecraft().currentScreen == null)
            PackedUp.CHANNEL.sendToServer(new PacketOpenBag());
    }

    public static ITextComponent getKeyBindCharacter(){
        return OPEN_BAG_KEY == null || OPEN_BAG_KEY.getKeyModifier() == KeyModifier.NONE ? null : TextComponents.string(OPEN_BAG_KEY.getDisplayName()).get();
    }
}
