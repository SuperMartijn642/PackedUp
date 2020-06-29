package com.supermartijn642.packedup;

import com.google.common.collect.Lists;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.*;

/**
 * Created 2/8/2020 by SuperMartijn642
 */
public class BackpackInventory implements IItemHandlerModifiable {

    private final boolean remote;
    private final ArrayList<ItemStack> stacks = new ArrayList<>();

    private final int inventoryIndex;
    public int rows;
    public Set<Integer> bagsInThisBag = new HashSet<>();
    public Set<Integer> bagsDirectlyInThisBag = new HashSet<>();
    public Set<Integer> bagsThisBagIsIn = new HashSet<>();
    public Set<Integer> bagsThisBagIsDirectlyIn = new HashSet<>();
    public int layer;

    public BackpackInventory(boolean remote, int inventoryIndex, int rows, Set<Integer> bagsInThisBag, Set<Integer> bagsThisBagIsIn, int layer){
        this.remote = remote;
        this.inventoryIndex = inventoryIndex;
        this.rows = rows;
        for(int a = 0; a < this.rows * 9; a++)
            this.stacks.add(ItemStack.EMPTY);
        this.bagsInThisBag.addAll(bagsInThisBag);
        this.bagsThisBagIsIn.addAll(bagsThisBagIsIn);
        this.layer = layer;
    }

    public BackpackInventory(boolean remote, int inventoryIndex, int rows){
        this.remote = remote;
        this.inventoryIndex = inventoryIndex;
        this.rows = rows;
        for(int a = 0; a < this.rows * 9; a++)
            this.stacks.add(ItemStack.EMPTY);
    }

    public BackpackInventory(boolean remote, int inventoryIndex){
        this.remote = remote;
        this.inventoryIndex = inventoryIndex;
    }

    @Override
    public int getSlots(){
        return this.rows * 9;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot){
        return this.stacks.get(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){
        if(stack.getItem() instanceof BackpackItem && !isBagAllowed(stack))
            return stack;

        ItemStack current = this.stacks.get(slot);
        if(!stack.isEmpty() && this.isItemValid(slot, stack) && canStack(current, stack)){
            int amount = Math.min(stack.getCount(), 64 - current.getCount());
            if(!simulate){
                ItemStack newStack = stack.copy();
                newStack.setCount(current.getCount() + amount);
                this.stacks.set(slot, newStack);

                if(!this.remote && stack.getItem() instanceof BackpackItem && stack.getOrCreateTag().contains("packedup:invIndex")){
                    int index = stack.getOrCreateTag().getInt("packedup:invIndex");
                    if(!this.bagsDirectlyInThisBag.contains(index))
                        BackpackStorageManager.onInsert(index, this.inventoryIndex);
                }
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
        if(!simulate){
            stack.shrink(count);

            if(!this.remote && result.getItem() instanceof BackpackItem && result.getOrCreateTag().contains("packedup:invIndex")){
                int index = result.getOrCreateTag().getInt("packedup:invIndex");
                boolean contains = false;
                for(ItemStack stack1 : this.stacks){
                    if(stack1.getItem() instanceof BackpackItem && stack1.getOrCreateTag().contains("packedup:invIndex")
                        && stack1.getOrCreateTag().getInt("packedup:invIndex") == index){
                        contains = true;
                        break;
                    }
                }
                if(!contains)
                    BackpackStorageManager.onExtract(index, this.inventoryIndex);
            }
        }
        result.setCount(count);
        return result;
    }

    @Override
    public int getSlotLimit(int slot){
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack){
        if(stack.getItem() instanceof BlockItem && ((BlockItem)stack.getItem()).getBlock() instanceof ShulkerBoxBlock && stack.hasTag()){
            CompoundNBT compound = stack.getTag().getCompound("BlockEntityTag");
            if(compound.contains("Items", 9)){
                NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
                ItemStackHelper.loadAllItems(compound, items);
                for(ItemStack stack1 : items)
                    if(stack1.getItem() instanceof BackpackItem)
                        return false;
            }
        }
        return true;
    }

    private static boolean canStack(ItemStack stack1, ItemStack stack2){
        return stack1.isEmpty() || stack2.isEmpty() || (stack1.getItem() == stack2.getItem() && stack1.getDamage() == stack2.getDamage() && ItemStack.areItemStackTagsEqual(stack1, stack2));
    }

    public void save(File file){
        CompoundNBT compound = new CompoundNBT();
        compound.putInt("rows", this.rows);
        compound.putInt("stacks", this.stacks.size());
        for(int slot = 0; slot < this.stacks.size(); slot++)
            compound.put("stack" + slot, this.stacks.get(slot).write(new CompoundNBT()));
        compound.putIntArray("bagsInThisBag", Lists.newArrayList(this.bagsInThisBag));
        compound.putIntArray("bagsThisBagIsIn", Lists.newArrayList(this.bagsThisBagIsIn));
        compound.putInt("layer", this.layer);
        try{
            CompressedStreamTools.write(compound, file);
        }catch(Exception e){e.printStackTrace();}
    }

    public void load(File file){
        CompoundNBT compound;
        try{
            compound = CompressedStreamTools.read(file);
        }catch(Exception e){
            e.printStackTrace();
            return;
        }
        this.rows = compound.contains("rows") ? compound.getInt("rows") : compound.getInt("slots") / 9; // Do this for compatibility with older versions
        this.stacks.clear();
        int size = compound.contains("stacks") ? compound.getInt("stacks") : this.rows * 9; // Do this for compatibility with older versions
        for(int slot = 0; slot < size; slot++)
            this.stacks.add(ItemStack.read(compound.getCompound("stack" + slot)));
        this.bagsInThisBag.clear();
        Arrays.stream(compound.getIntArray("bagsInThisBag")).forEach(this.bagsInThisBag::add);
        this.bagsThisBagIsIn.clear();
        Arrays.stream(compound.getIntArray("bagsThisBagIsIn")).forEach(this.bagsThisBagIsIn::add);
        this.layer = compound.getInt("layer");
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack){
        ItemStack oldStack = this.stacks.get(slot);
        this.stacks.set(slot, ItemStack.EMPTY);

        if(!this.remote && oldStack.getItem() instanceof BackpackItem && oldStack.getOrCreateTag().contains("packedup:invIndex")){
            int index = oldStack.getOrCreateTag().getInt("packedup:invIndex");
            boolean contains = false;
            for(ItemStack stack1 : this.stacks){
                if(stack1.getItem() instanceof BackpackItem && stack1.getOrCreateTag().contains("packedup:invIndex")
                    && stack1.getOrCreateTag().getInt("packedup:invIndex") == index){
                    contains = true;
                    break;
                }
            }
            if(!contains)
                BackpackStorageManager.onExtract(index, this.inventoryIndex);
        }

        this.stacks.set(slot, stack);

        if(!this.remote && stack.getItem() instanceof BackpackItem && stack.getOrCreateTag().contains("packedup:invIndex")){
            int index = stack.getOrCreateTag().getInt("packedup:invIndex");
            if(!this.bagsDirectlyInThisBag.contains(index))
                BackpackStorageManager.onInsert(index, this.inventoryIndex);
        }
    }

    public void adjustSize(int rows){
        if(this.rows == rows)
            return;
        this.rows = rows;
        while(this.stacks.size() < this.rows * 9)
            this.stacks.add(ItemStack.EMPTY);
    }

    public List<ItemStack> getStacks(){
        return this.stacks;
    }

    private boolean isBagAllowed(ItemStack bag){
        if(BackpackStorageManager.maxLayers != -1 && this.layer >= BackpackStorageManager.maxLayers)
            return false;
        if(!bag.getOrCreateTag().contains("packedup:invIndex"))
            return true;
        int index = bag.getOrCreateTag().getInt("packedup:invIndex");
        return index != this.inventoryIndex && !this.bagsThisBagIsIn.contains(index);
    }
}
