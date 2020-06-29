package com.supermartijn642.packedup;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent e){
        for(BackpackType type : BackpackType.values()){
            Item backpack = ForgeRegistries.ITEMS.getValue(new ResourceLocation(PackedUp.MODID, type.getRegistryName()));
            ModelLoader.setCustomModelResourceLocation(backpack, 0, new ModelResourceLocation(backpack.getRegistryName(), "inventory"));
        }
    }

    public static EntityPlayer getClientPlayer(){
        return Minecraft.getMinecraft().player;
    }

    public static void openScreen(String defaultName, String name){
        Minecraft.getMinecraft().displayGuiScreen(new BackpackRenameScreen(defaultName, name));
    }

    public static void addScheduledTask(Runnable task){
        Minecraft.getMinecraft().addScheduledTask(task);
    }

    public static GuiScreen getGui(){
        return Minecraft.getMinecraft().currentScreen;
    }
}
