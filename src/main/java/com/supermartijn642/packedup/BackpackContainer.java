package com.supermartijn642.packedup;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Set;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public class BackpackContainer extends Container {

    public final int rows;
    public final int bagSlot;

    public BackpackContainer(int id, PlayerInventory player, int bagSlot, int inventoryIndex, int rows, Set<Integer> bagsInThisBag, Set<Integer> bagsThisBagIsIn, int layer){
        super(PackedUp.container, id);
        this.bagSlot = bagSlot;
        this.rows = rows;

        BackpackInventory inventory = player.player.world.isRemote ?
            new BackpackInventory(true, inventoryIndex, rows, bagsInThisBag, bagsThisBagIsIn, layer) :
            BackpackStorageManager.getInventory(inventoryIndex);

        this.addSlots(this.rows, inventory, player);
    }

    private void addSlots(int rows, IItemHandler inventory, PlayerInventory player){
        int startX = 8;
        int startY = rows < 9 ? 17 : 8;

        int columns = 9 + Math.max(rows - 9, 0);
        rows = Math.min(rows, 9);
        for(int row = 0; row < rows; row++){
            for(int column = 0; column < columns; column++){
                int x = startX + 18 * column, y = startY + 18 * row, index = row * columns + column;
                this.addSlot(new SlotItemHandler(inventory, index, x, y));
            }
        }

        startX = 8 + (columns - 9) * 9;
        startY += rows * 18 + (rows == 9 ? 4 : 13);

        // player slots
        for(int row = 0; row < 3; row++){
            for(int column = 0; column < 9; column++){
                int x = startX + 18 * column, y = startY + 18 * row, index = row * 9 + column + 9;
                if(index == this.bagSlot)
                    this.addSlot(new Slot(player, index, x, y) {
                        public boolean canTakeStack(PlayerEntity playerIn){
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

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player){
        if(clickTypeIn == ClickType.SWAP && dragType == this.bagSlot)
            return ItemStack.EMPTY;
        if(clickTypeIn == ClickType.PICKUP && dragType == 1){
            Slot slot = this.getSlot(slotId);
            if(slot.canTakeStack(player)){
                ItemStack stack = slot.getStack();
                if(stack.getItem() instanceof BackpackItem){
                    if(!player.world.isRemote){
                        int bagSlot = slotId >= (this.rows + 3) * 9 ? slotId - (this.rows + 3) * 9 : slotId >= this.rows * 9 ? slotId - (this.rows - 1) * 9 : -1;
                        CommonProxy.openBackpackInventory(stack, player, bagSlot);
                    }
                    return ItemStack.EMPTY;
                }
            }
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }
}
