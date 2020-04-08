package com.supermartijn642.packedup;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
@Mod.EventBusSubscriber
public class CommonProxy {

    @SubscribeEvent
    public static void onItemRegistry(final RegistryEvent.Register<Item> e){
        for(BackpackType type : BackpackType.values())
            e.getRegistry().register(new BackpackItem(type));
    }

    public EntityPlayer getClientPlayer(){
        return null;
    }

}
