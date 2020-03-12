package com.supermartijn642.packedup;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void init(){
        ScreenManager.registerFactory(PackedUp.container, BackpackContainerScreen::new);
    }

    @Override
    public PlayerEntity getClientPlayer(){
        return Minecraft.getInstance().player;
    }
}
