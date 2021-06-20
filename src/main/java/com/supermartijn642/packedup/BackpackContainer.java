package com.supermartijn642.packedup;

import com.supermartijn642.core.gui.BaseContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public class BackpackContainer extends BaseContainer {

    public final BackpackInventory inventory;
    public final BackpackType type;
    public final int bagSlot;

    public BackpackContainer(InventoryPlayer player, int bagSlot, int inventoryIndex, BackpackType type){
        super(player.player);
        this.bagSlot = bagSlot;
        this.type = type;

        this.inventory = player.player.world.isRemote ?
            new BackpackInventory(true, inventoryIndex, type.getSlots()) :
            BackpackStorageManager.getInventory(inventoryIndex);

        this.addSlots(this.inventory, player);
    }

    private void addSlots(IItemHandler inventory, InventoryPlayer player){
        int startX = Math.max(0, 9 - this.type.getColumns()) * 18 / 2 + 8;
        int startY = 17;

        for(int row = 0; row < this.type.getRows(); row++){
            for(int column = 0; column < this.type.getColumns(); column++){
                int x = startX + 18 * column, y = startY + 18 * row, index = row * this.type.getColumns() + column;
                this.addSlot(new SlotItemHandler(inventory, index, x, y));
            }
        }

        startX = Math.max(0, this.type.getColumns() - 9) * 18 / 2 + 8;
        startY += this.type.getRows() * 18 + 13;

        // player slots
        for(int row = 0; row < 3; row++){
            for(int column = 0; column < 9; column++){
                int x = startX + 18 * column, y = startY + 18 * row, index = row * 9 + column + 9;
                if(index == this.bagSlot)
                    this.addSlot(new Slot(player, index, x, y) {
                        public boolean canTakeStack(EntityPlayer playerIn){
                            return false;
                        }
                    });
                else
                    this.addSlot(new Slot(player, index, x, y));
            }
        }

        startY += 58;

        for(int column = 0; column < 9; column++){
            int x = startX + 18 * column, y = startY;
            if(column == this.bagSlot)
                this.addSlot(new Slot(player, column, x, y) {
                    public boolean canTakeStack(EntityPlayer player){
                        return false;
                    }
                });
            else
                this.addSlot(new Slot(player, column, x, y));
        }
    }

    @Override
    protected void addSlots(EntityPlayer playerEntity){
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
            if(index < this.type.getSlots()){
                if(!this.mergeItemStack(slotStack, this.type.getSlots(), this.inventorySlots.size(), true)){
                    return ItemStack.EMPTY;
                }
            }else if(!this.mergeItemStack(slotStack, 0, this.type.getSlots(), false)){
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
        if(clickTypeIn == ClickType.PICKUP && dragType == 1 && slotId >= 0){
            Slot slot = this.getSlot(slotId);
            if(slot.canTakeStack(player)){
                ItemStack stack = slot.getStack();
                if(stack.getItem() instanceof BackpackItem){
                    if(!player.world.isRemote){
                        int bagSlot = slotId >= this.type.getSlots() + 27 ? slotId - this.type.getSlots() - 27 : slotId >= this.type.getSlots() ? slotId - this.type.getSlots() - 9 : -1;
                        CommonProxy.openBackpackInventory(stack, player, bagSlot);
                    }
                    return ItemStack.EMPTY;
                }
            }
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }
}
