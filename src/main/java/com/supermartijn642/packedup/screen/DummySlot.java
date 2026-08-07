package com.supermartijn642.packedup.screen;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

/**
 * Created 29/01/2023 by SuperMartijn642
 */
public class DummySlot extends Slot {

    public DummySlot(IInventory container, int index, int x, int y){
        super(container, index, x, y);
    }

    @Override
    public ItemStack getItem(){
        return ItemStack.EMPTY;
    }

    @Override
    public void set(ItemStack itemStack){
    }

    @Override
    public void setChanged(){
    }

    @Override
    public int getMaxStackSize(){
        ItemStack stack = this.getItem();
        return stack.isEmpty() ? super.getMaxStackSize() : stack.getMaxStackSize();
    }

    @Override
    public int getMaxStackSize(ItemStack stack){
        return stack.getMaxStackSize();
    }

    @Override
    public ItemStack remove(int count){
        ItemStack stack = this.getItem();
        ItemStack result = stack.split(count);
        this.set(stack);
        return result;
    }
}
