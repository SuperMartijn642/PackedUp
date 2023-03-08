package com.supermartijn642.packedup;

import com.google.common.collect.Lists;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;

import java.io.File;
import java.util.*;

/**
 * Created 2/8/2020 by SuperMartijn642
 */
public class BackpackInventory {

    private final boolean remote;
    private final ArrayList<ItemStack> stacks = new ArrayList<>();

    private final int inventoryIndex;
    public Set<Integer> bagsInThisBag = new HashSet<>();
    public Set<Integer> bagsDirectlyInThisBag = new HashSet<>();
    public Set<Integer> bagsThisBagIsIn = new HashSet<>();
    public Set<Integer> bagsThisBagIsDirectlyIn = new HashSet<>();
    public int layer;

    public BackpackInventory(boolean remote, int inventoryIndex, int slots, Set<Integer> bagsInThisBag, Set<Integer> bagsThisBagIsIn, int layer){
        this.remote = remote;
        this.inventoryIndex = inventoryIndex;
        for(int a = 0; a < slots; a++)
            this.stacks.add(ItemStack.EMPTY);
        this.bagsInThisBag.addAll(bagsInThisBag);
        this.bagsThisBagIsIn.addAll(bagsThisBagIsIn);
        this.layer = layer;
    }

    public BackpackInventory(boolean remote, int inventoryIndex, int slots){
        this.remote = remote;
        this.inventoryIndex = inventoryIndex;
        for(int a = 0; a < slots; a++)
            this.stacks.add(ItemStack.EMPTY);
    }

    public BackpackInventory(boolean remote, int inventoryIndex){
        this.remote = remote;
        this.inventoryIndex = inventoryIndex;
    }

    public int getInventoryIndex(){
        return this.inventoryIndex;
    }

    public ItemStack getStackInSlot(int slot){
        return this.stacks.get(slot);
    }

    public ItemStack extractItem(int slot, int amount){
        ItemStack stack = this.stacks.get(slot);
        int count = Math.min(amount, stack.getCount());
        ItemStack result = stack.copy();
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
        result.setCount(count);
        return result;
    }

    public boolean isItemValid(ItemStack stack){
        if(stack.getItem() instanceof BackpackItem && !this.isBagAllowed(stack))
            return false;

        if(stack.getItem() instanceof BlockItem && ((BlockItem)stack.getItem()).getBlock() instanceof ShulkerBoxBlock && stack.hasTag()){
            CompoundTag compound = stack.getTag().getCompound("BlockEntityTag");
            if(compound.contains("Items", 9)){
                NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
                ContainerHelper.loadAllItems(compound, items);
                for(ItemStack stack1 : items)
                    if(stack1.getItem() instanceof BackpackItem)
                        return false;
            }
        }
        return true;
    }

    private static boolean canStack(ItemStack stack1, ItemStack stack2){
        return stack1.isEmpty() || stack2.isEmpty() || (stack1.getItem() == stack2.getItem() && stack1.getDamageValue() == stack2.getDamageValue() && ItemStack.isSameIgnoreDurability(stack1, stack2));
    }

    public void save(File file){
        CompoundTag compound = new CompoundTag();
        compound.putInt("stacks", this.stacks.size());
        for(int slot = 0; slot < this.stacks.size(); slot++)
            compound.put("stack" + slot, this.stacks.get(slot).save(new CompoundTag()));
        compound.putIntArray("bagsInThisBag", Lists.newArrayList(this.bagsInThisBag));
        compound.putIntArray("bagsThisBagIsIn", Lists.newArrayList(this.bagsThisBagIsIn));
        compound.putInt("layer", this.layer);
        try{
            NbtIo.write(compound, file);
        }catch(Exception e){e.printStackTrace();}
    }

    public void load(File file){
        CompoundTag compound;
        try{
            compound = NbtIo.read(file);
        }catch(Exception e){
            e.printStackTrace();
            return;
        }
        this.stacks.clear();
        int size = compound.contains("stacks") ? compound.getInt("stacks") : compound.contains("rows") ? compound.getInt("rows") * 9 : compound.getInt("slots"); // Do this for compatibility with older versions
        for(int slot = 0; slot < size; slot++)
            this.stacks.add(ItemStack.of(compound.getCompound("stack" + slot)));
        this.bagsInThisBag.clear();
        Arrays.stream(compound.getIntArray("bagsInThisBag")).forEach(this.bagsInThisBag::add);
        this.bagsThisBagIsIn.clear();
        Arrays.stream(compound.getIntArray("bagsThisBagIsIn")).forEach(this.bagsThisBagIsIn::add);
        this.layer = compound.getInt("layer");
    }

    public void setStackInSlot(int slot, ItemStack stack){
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

    public void adjustSize(BackpackType type){
        while(this.stacks.size() < type.getSlots())
            this.stacks.add(ItemStack.EMPTY);
    }

    public List<ItemStack> getStacks(){
        return this.stacks;
    }

    private boolean isBagAllowed(ItemStack bag){
        if(BackpackStorageManager.maxLayers.get() != -1 && this.layer >= BackpackStorageManager.maxLayers.get())
            return false;
        if(!bag.getOrCreateTag().contains("packedup:invIndex"))
            return true;
        int index = bag.getOrCreateTag().getInt("packedup:invIndex");
        return index != this.inventoryIndex && !this.bagsThisBagIsIn.contains(index);
    }
}
