package com.supermartijn642.packedup;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.gui.WidgetContainerScreen;
import com.supermartijn642.core.gui.WidgetScreen;
import com.supermartijn642.core.registry.ClientRegistrationHandler;
import com.supermartijn642.packedup.packets.PacketOpenBag;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public class PackedUpClient implements ClientModInitializer {

    public static KeyMapping OPEN_BAG_KEY;

    @Override
    public void onInitializeClient(){
        ClientRegistrationHandler handler = ClientRegistrationHandler.get("packedup");
        // Register screen for the backpack inventory
        handler.registerContainerScreen(() -> PackedUp.container, container -> WidgetContainerScreen.of(new BackpackContainerScreen(), container, true));

        // Register key to open backpack in inventory or curious slot
        OPEN_BAG_KEY = new KeyMapping("packedup.keys.openbag", 79/*'o'*/, "packedup.keys.category");
        KeyBindingHelper.registerKeyBinding(OPEN_BAG_KEY);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(OPEN_BAG_KEY.consumeClick())
                onKey();
        });
    }

    public static void openBackpackRenameScreen(String defaultName, String name){
        ClientUtils.displayScreen(WidgetScreen.of(new BackpackRenameScreen(defaultName, name)));
    }

    public static void onKey(){
        if(ClientUtils.getWorld() != null && ClientUtils.getMinecraft().screen == null)
            PackedUp.CHANNEL.sendToServer(new PacketOpenBag());
    }

    public static Component getKeyBindCharacter(){
        return OPEN_BAG_KEY == null || OPEN_BAG_KEY.isUnbound() ? null : OPEN_BAG_KEY.getTranslatedKeyMessage();
    }
}
