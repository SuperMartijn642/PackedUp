package com.supermartijn642.packedup;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

/**
 * Created 4/8/2020 by SuperMartijn642
 */
public class GuiHandler implements IGuiHandler {
    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
        int bagSlot = ((ID >> 5) & 63) - 2;
        int inventoryIndex = (ID >> 11) & 2097151;
        return new BackpackContainer(inventoryIndex, player, bagSlot);
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
        int rows = ID & 31;
        int bagSlot = ((ID >> 5) & 63) - 2;
        return new BackpackContainerScreen(new BackpackContainer(rows, player, bagSlot), player);
    }
}
