package com.supermartijn642.packedup;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public class BackpackContainer extends Container {

    public final int rows;

    public BackpackContainer(int id, int inventoryIndex, PlayerInventory player, int bagSlot){
        super(PackedUp.container, id);

        BackpackInventory inventory = player.player.world.isRemote ? new BackpackInventory(inventoryIndex * 9) : BackpackStorageManager.getInventory(inventoryIndex);
        this.rows = inventory.getSlots() / 9;
        this.addSlots(this.rows, inventory, player, bagSlot);
    }

    private void addSlots(int rows, IItemHandler inventory, PlayerInventory player, int bagSlot){
        for(int row = 0; row < rows; row++){
            for(int column = 0; column < 9; column++){
                int x = 8 + 18 * column, y = 17 + 18 * row, index = row * 9 + column;
                this.addSlot(new SlotItemHandler(inventory, index, x, y));
            }
        }
        // player slots
        for(int row = 0; row < 3; row++){
            for(int column = 0; column < 9; column++){
                int x = 8 + 18 * column, y = 30 + 18 * rows + 18 * row, index = row * 9 + column + 9;
                this.addSlot(new Slot(player, index, x, y));
            }
        }
        for(int column = 0; column < 9; column++){
            int x = 8 + 18 * column, y = 88 + 18 * rows;
            if(column == bagSlot)
                this.addSlot(new Slot(player, column, x, y) {
                    public boolean canTakeStack(PlayerEntity playerIn){
                        return false;
                    }
                });
            else
                this.addSlot(new Slot(player, column, x, y));
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn){
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index){
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
}
