package com.supermartijn642.packedup;

import com.supermartijn642.packedup.packets.PacketOpenBag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public class ClientProxy extends CommonProxy {

    private static KeyBinding OPEN_BAG_KEY;

    @Override
    public void init(){
        ScreenManager.register(PackedUp.container, BackpackContainerScreen::new);

        OPEN_BAG_KEY = new KeyBinding("keys.packedup.openbag", 79/*'o'*/, "keys.category.packedup");
        ClientRegistry.registerKeyBinding(OPEN_BAG_KEY);
        MinecraftForge.EVENT_BUS.addListener(ClientProxy::onKey);
    }

    @Override
    public PlayerEntity getClientPlayer(){
        return Minecraft.getInstance().player;
    }

    public static void openScreen(String defaultName, String name){
        Minecraft.getInstance().setScreen(new BackpackRenameScreen(defaultName, name));
    }

    public static void onKey(InputEvent.KeyInputEvent e){
        if(OPEN_BAG_KEY != null && OPEN_BAG_KEY.matches(e.getKey(), e.getScanCode()) && Minecraft.getInstance().level != null && Minecraft.getInstance().screen == null)
            PackedUp.CHANNEL.sendToServer(new PacketOpenBag());
    }

    public static ITextComponent getKeyBindCharacter(){
        return OPEN_BAG_KEY == null || OPEN_BAG_KEY.getKey().getValue() == -1 ? null : OPEN_BAG_KEY.getKey().getDisplayName();
    }
}
