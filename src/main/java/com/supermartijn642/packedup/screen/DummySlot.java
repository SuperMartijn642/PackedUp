package com.supermartijn642.packedup.screen;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Created 29/01/2023 by SuperMartijn642
 */
public class DummySlot extends Slot {

    public final int index;

    public DummySlot(IInventory container, int index, int x, int y){
        super(container, index, x, y);
        this.index = index;
    }

    @Override
    public ItemStack getStack(){
        return ItemStack.EMPTY;
    }

    @Override
    public void putStack(ItemStack itemStack){
    }

    @Override
    public void onSlotChanged(){
    }

    @Override
    public int getSlotStackLimit(){
        ItemStack stack = this.getStack();
        return stack.isEmpty() ? 64 : stack.getMaxStackSize();
    }

    @Override
    public int getItemStackLimit(ItemStack stack){
        return stack.getMaxStackSize();
    }

    @Override
    public ItemStack decrStackSize(int count){
        ItemStack stack = this.getStack();
        ItemStack result = stack.splitStack(count);
        this.putStack(stack);
        return result;
    }
}
