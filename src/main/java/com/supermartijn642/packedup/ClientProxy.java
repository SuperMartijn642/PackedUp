package com.supermartijn642.packedup;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.packedup.packets.PacketOpenBag;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy {

    private static KeyMapping OPEN_BAG_KEY;

    @SubscribeEvent
    public static void init(FMLClientSetupEvent e){
        MenuScreens.register(PackedUp.container, (MenuScreens.ScreenConstructor<BackpackContainer,BackpackContainerScreen>)((container, b, name) -> new BackpackContainerScreen(container, name)));

        OPEN_BAG_KEY = new KeyMapping("keys.packedup.openbag", 79/*'o'*/, "keys.category.packedup");
        ClientRegistry.registerKeyBinding(OPEN_BAG_KEY);
        MinecraftForge.EVENT_BUS.addListener(ClientProxy::onKey);
    }

    public static void openScreen(String defaultName, String name){
        ClientUtils.displayScreen(new BackpackRenameScreen(defaultName, name));
    }

    public static void onKey(InputEvent.KeyInputEvent e){
        if(OPEN_BAG_KEY != null && OPEN_BAG_KEY.matches(e.getKey(), e.getScanCode()) && ClientUtils.getWorld() != null && ClientUtils.getMinecraft().screen == null)
            PackedUp.CHANNEL.sendToServer(new PacketOpenBag());
    }

    public static Component getKeyBindCharacter(){
        return OPEN_BAG_KEY == null || OPEN_BAG_KEY.getKey().getValue() == -1 ? null : OPEN_BAG_KEY.getKey().getDisplayName();
    }
}
