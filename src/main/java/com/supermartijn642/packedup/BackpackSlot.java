package com.supermartijn642.packedup;

import net.minecraft.world.item.ItemStack;

/**
 * Created 08/03/2023 by SuperMartijn642
 */
public class BackpackSlot extends DummySlot {

    private final BackpackInventory inventory;

    public BackpackSlot(BackpackInventory inventory, int index, int x, int y){
        super(index, x, y);
        this.inventory = inventory;
    }

    @Override
    public void initialize(ItemStack stack){
        this.set(stack);
    }

    @Override
    public ItemStack getItem(){
        return this.inventory.getStackInSlot(this.index);
    }

    @Override
    public void set(ItemStack stack){
        this.inventory.setStackInSlot(this.index, stack);
    }

    @Override
    public ItemStack remove(int count){
        return this.inventory.extractItem(this.index, count);
    }

    @Override
    public boolean mayPlace(ItemStack stack){
        return this.inventory.isItemValid(stack);
    }

    @Override
    public int getMaxStackSize(){
        return 64;
    }
}
