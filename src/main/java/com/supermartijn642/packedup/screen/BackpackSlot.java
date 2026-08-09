package com.supermartijn642.packedup.screen;

import com.supermartijn642.packedup.storage.BackpackInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

/**
 * Created 08/03/2023 by SuperMartijn642
 */
public class BackpackSlot extends DummySlot {

    private final BackpackInventory inventory;

    public BackpackSlot(BackpackInventory inventory, int index, int x, int y){
        super(new BackpackInventoryAsContainer(inventory), index, x, y);
        this.inventory = inventory;
    }

    @Override
    public ItemStack getStack(){
        return this.inventory.getStackInSlot(this.index);
    }

    @Override
    public void putStack(ItemStack stack){
        this.inventory.setStackInSlot(this.index, stack);
    }

    @Override
    public ItemStack decrStackSize(int count){
        return this.inventory.extractItem(this.index, count);
    }

    @Override
    public boolean isItemValid(ItemStack stack){
        return this.inventory.isItemValid(stack);
    }

    private static class BackpackInventoryAsContainer implements IInventory {

        private final BackpackInventory inventory;

        private BackpackInventoryAsContainer(BackpackInventory inventory){
            this.inventory = inventory;
        }

        @Override
        public void clear(){
            int size = this.inventory.getStacks().size();
            for(int i = 0; i < size; i++)
                this.inventory.setStackInSlot(i, ItemStack.EMPTY);
        }

        @Override
        public int getSizeInventory(){
            return this.inventory.getStacks().size();
        }

        @Override
        public boolean isEmpty(){
            for(ItemStack stack : this.inventory.getStacks()){
                if(!stack.isEmpty())
                    return false;
            }
            return true;
        }

        @Override
        public ItemStack getStackInSlot(int slot){
            return this.inventory.getStackInSlot(slot);
        }

        @Override
        public ItemStack decrStackSize(int slot, int count){
            return this.inventory.extractItem(slot, count);
        }

        @Override
        public ItemStack removeStackFromSlot(int slot){
            ItemStack stack = this.getStackInSlot(slot);
            return this.decrStackSize(slot, stack.getCount());
        }

        @Override
        public void setInventorySlotContents(int slot, ItemStack stack){
            this.inventory.setStackInSlot(slot, stack);
        }

        @Override
        public int getInventoryStackLimit(){
            return Integer.MAX_VALUE;
        }

        @Override
        public void markDirty(){
        }

        @Override
        public boolean isUsableByPlayer(EntityPlayer player){
            return true;
        }

        @Override
        public void openInventory(EntityPlayer player){
            throw new IllegalStateException("This should not be called for a slot container!");
        }

        @Override
        public void closeInventory(EntityPlayer player){
            throw new IllegalStateException("This should not be called for a slot container!");
        }

        @Override
        public boolean isItemValidForSlot(int slot, ItemStack stack){
            return this.inventory.isItemValid(stack);
        }

        @Override
        public int getField(int id){
            return 0;
        }

        @Override
        public void setField(int id, int value){
        }

        @Override
        public int getFieldCount(){
            return 0;
        }

        @Override
        public String getName(){
            return "";
        }

        @Override
        public boolean hasCustomName(){
            return false;
        }

        @Override
        public ITextComponent getDisplayName(){
            return null;
        }
    }
}
