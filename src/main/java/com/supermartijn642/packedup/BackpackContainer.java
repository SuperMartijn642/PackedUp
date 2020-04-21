package com.supermartijn642.packedup;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public class BackpackContainer extends Container {

    public final int rows;
    public final int bagSlot;

    public BackpackContainer(EntityPlayer player, int rows){
        this.rows = rows;
        this.bagSlot = player.inventory.getCurrentItem().getItem() instanceof BackpackItem ? player.inventory.currentItem : -1;

        ItemStack backpack = player.getHeldItemMainhand().getItem() instanceof BackpackItem ? player.getHeldItemMainhand() : player.getHeldItemOffhand();
        BackpackInventory inventory = player.world.isRemote ? new BackpackInventory(this.rows * 9) : BackpackStorageManager.getInventory(backpack.getTagCompound().getInteger("packedup:invIndex"));

        this.addSlots(this.rows, inventory, player);
    }

    private void addSlots(int rows, IItemHandler inventory, EntityPlayer player){
        int startX = 8;
        int startY = rows < 9 ? 17 : 8;

        int columns = 9 + Math.max(rows - 9, 0);
        rows = Math.min(rows, 9);
        for(int row = 0; row < rows; row++){
            for(int column = 0; column < columns; column++){
                int x = startX + 18 * column, y = startY + 18 * row, index = row * columns + column;
                this.addSlotToContainer(new SlotItemHandler(inventory, index, x, y));
            }
        }

        startX = 8 + (columns - 9) * 9;
        startY += rows * 18 + (rows == 9 ? 4 : 13);

        // player slots
        for(int row = 0; row < 3; row++){
            for(int column = 0; column < 9; column++){
                int x = startX + 18 * column, y = startY + 18 * row, index = row * 9 + column + 9;
                this.addSlotToContainer(new Slot(player.inventory, index, x, y));
            }
        }

        startY += 58;

        for(int column = 0; column < 9; column++){
            int x = startX + 18 * column, y = startY;
            if(column == this.bagSlot)
                this.addSlotToContainer(new Slot(player.inventory, column, x, y) {
                    public boolean canTakeStack(EntityPlayer playerIn){
                        return false;
                    }
                });
            else
                this.addSlotToContainer(new Slot(player.inventory, column, x, y));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn){
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index){
        ItemStack returnStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if(slot != null && slot.getHasStack()){
            ItemStack slotStack = slot.getStack();
            returnStack = slotStack.copy();
            if(index < this.rows * 9){
                if(!this.mergeItemStack(slotStack, this.rows * 9, this.inventorySlots.size(), true)){
                    return ItemStack.EMPTY;
                }
            }else if(!this.mergeItemStack(slotStack, 0, this.rows * 9, false)){
                return ItemStack.EMPTY;
            }

            if(slotStack.isEmpty()){
                slot.putStack(ItemStack.EMPTY);
            }else{
                slot.onSlotChanged();
            }
        }

        return returnStack;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player){
        if(clickTypeIn == ClickType.SWAP && dragType == this.bagSlot)
            return ItemStack.EMPTY;
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }
}
