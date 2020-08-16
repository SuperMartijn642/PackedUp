package com.supermartijn642.packedup;

import com.supermartijn642.packedup.packets.PacketOpenBag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public class ClientProxy extends CommonProxy {

    private static KeyBinding OPEN_BAG_KEY = new KeyBinding("keys.packedup.openbag", 79/*'o'*/, "keys.category.packedup");

    @Override
    public void init(){
        ScreenManager.registerFactory(PackedUp.container, BackpackContainerScreen::new);

        if(ModList.get().isLoaded("curios")){
            ClientRegistry.registerKeyBinding(OPEN_BAG_KEY);
            MinecraftForge.EVENT_BUS.addListener(ClientProxy::onKey);
        }
    }

    @Override
    public PlayerEntity getClientPlayer(){
        return Minecraft.getInstance().player;
    }

    public static void openScreen(String defaultName, String name){
        Minecraft.getInstance().displayGuiScreen(new BackpackRenameScreen(defaultName, name));
    }

    public static void onKey(InputEvent.KeyInputEvent e){
        if(OPEN_BAG_KEY.matchesKey(e.getKey(), e.getScanCode()) && Minecraft.getInstance().world != null)
            PackedUp.CHANNEL.sendToServer(new PacketOpenBag());
    }
}
