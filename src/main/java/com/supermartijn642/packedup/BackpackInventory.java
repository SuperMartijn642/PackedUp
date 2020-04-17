package com.supermartijn642.packedup;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;

/**
 * Created 2/8/2020 by SuperMartijn642
 */
public class BackpackInventory implements IItemHandlerModifiable {

    private final ArrayList<ItemStack> stacks = new ArrayList<>();
    private int slots;

    public BackpackInventory(int slots){
        this.slots = slots;
        for(int a = 0; a < slots; a++)
            this.stacks.add(ItemStack.EMPTY);
    }

    public BackpackInventory(){
    }

    @Override
    public int getSlots(){
        return this.slots;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot){
        return this.stacks.get(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){
        ItemStack current = this.stacks.get(slot);
        if(!stack.isEmpty() && canStack(current, stack)){
            int amount = Math.min(stack.getCount(), 64 - current.getCount());
            if(!simulate){
                ItemStack newStack = stack.copy();
                newStack.setCount(current.getCount() + amount);
                this.stacks.set(slot, newStack);
            }
            ItemStack result = stack.copy();
            result.shrink(amount);
            return result;
        }
        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate){
        ItemStack stack = this.stacks.get(slot);
        int count = Math.min(amount, stack.getCount());
        ItemStack result = stack.copy();
        if(!simulate)
            stack.shrink(count);
        result.setCount(count);
        return result;
    }

    @Override
    public int getSlotLimit(int slot){
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack){
        return true;
    }

    private static boolean canStack(ItemStack stack1, ItemStack stack2){
        return stack1.isEmpty() || stack2.isEmpty() || (stack1.getItem() == stack2.getItem() && stack1.getItemDamage() == stack2.getItemDamage() && ItemStack.areItemStackTagsEqual(stack1, stack2));
    }

    public void save(File file){
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("slots", this.slots);
        compound.setInteger("stacks", this.stacks.size());
        for(int slot = 0; slot < this.slots; slot++)
            compound.setTag("stack" + slot, this.stacks.get(slot).writeToNBT(new NBTTagCompound()));
        try{
            CompressedStreamTools.write(compound, file);
        }catch(Exception e){e.printStackTrace();}
    }

    public void load(File file){
        NBTTagCompound compound;
        try{
            compound = CompressedStreamTools.read(file);
        }catch(Exception e){
            e.printStackTrace();
            return;
        }
        this.slots = compound.getInteger("slots");
        this.stacks.clear();
        int size = compound.hasKey("stacks") ? compound.getInteger("stacks") : this.slots; // Do this for compatibility with older versions
        for(int slot = 0; slot < size; slot++)
            this.stacks.add(new ItemStack(compound.getCompoundTag("stack" + slot)));
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack){
        this.stacks.set(slot, stack);
    }

    public void adjustSize(int slots){
        if(this.slots == slots)
            return;
        this.slots = slots;
        while(this.stacks.size() < slots)
            this.stacks.add(ItemStack.EMPTY);
    }
}
