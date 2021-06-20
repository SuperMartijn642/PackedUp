package com.supermartijn642.packedup;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.TextComponents;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
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
        BackpackType type = BackpackType.values()[ID & 31];
        int bagSlot = ((ID >> 5) & 63) - 2;
        int inventoryIndex = (ID >> 11) & 2097151;
        return new BackpackContainer(player.inventory, bagSlot, inventoryIndex, type);
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
        BackpackType type = BackpackType.values()[ID & 31];
        int bagSlot = ((ID >> 5) & 63) - 2;
        int inventoryIndex = (ID >> 11) & 2097151;
        ITextComponent name = bagSlot >= 0 ? TextComponents.itemStack(ClientUtils.getPlayer().inventory.getStackInSlot(bagSlot)).get() : TextComponents.item(PackedUp.basicbackpack).get();
        return new BackpackContainerScreen(new BackpackContainer(player.inventory, bagSlot, inventoryIndex, type), name);
    }
}
