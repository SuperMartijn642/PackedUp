package com.supermartijn642.packedup;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.gui.WidgetContainerScreen;
import com.supermartijn642.core.gui.WidgetScreen;
import com.supermartijn642.core.registry.ClientRegistrationHandler;
import com.supermartijn642.packedup.packets.PacketOpenBag;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PackedUpClient {

    private static KeyBinding OPEN_BAG_KEY;

    public static void register(){
        ClientRegistrationHandler handler = ClientRegistrationHandler.get("packedup");
        // Register screen for the backpack inventory
        handler.registerContainerScreen(() -> PackedUp.container, container -> WidgetContainerScreen.of(new BackpackContainerScreen(), container, true));
    }

    @SubscribeEvent
    public static void registerKeyBindings(FMLClientSetupEvent e){
        // Register key to open backpack in inventory or curious slot
        OPEN_BAG_KEY = new KeyBinding("packedup.keys.openbag", 79/*'o'*/, "packedup.keys.category");
        ClientRegistry.registerKeyBinding(OPEN_BAG_KEY);
        MinecraftForge.EVENT_BUS.addListener(PackedUpClient::onKey);
    }

    public static void openBackpackRenameScreen(String defaultName, String name){
        ClientUtils.displayScreen(WidgetScreen.of(new BackpackRenameScreen(defaultName, name)));
    }

    public static void onKey(InputEvent.KeyInputEvent e){
        if(OPEN_BAG_KEY != null && OPEN_BAG_KEY.matches(e.getKey(), e.getScanCode()) && ClientUtils.getWorld() != null && ClientUtils.getMinecraft().screen == null)
            PackedUp.CHANNEL.sendToServer(new PacketOpenBag());
    }

    public static ITextComponent getKeyBindCharacter(){
        return OPEN_BAG_KEY == null || OPEN_BAG_KEY.getKey().getValue() == -1 ? null : OPEN_BAG_KEY.getKey().getDisplayName();
    }
}
