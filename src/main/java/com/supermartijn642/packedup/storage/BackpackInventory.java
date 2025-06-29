package com.supermartijn642.packedup.storage;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.supermartijn642.packedup.BackpackItem;
import com.supermartijn642.packedup.BackpackType;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.ShulkerBoxBlock;

import java.nio.file.Path;
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

        if(!this.remote && result.getItem() instanceof BackpackItem && result.has(BackpackItem.INVENTORY_ID)){
            //noinspection DataFlowIssue
            int index = result.get(BackpackItem.INVENTORY_ID);
            boolean contains = false;
            for(ItemStack stack1 : this.stacks){
                //noinspection DataFlowIssue
                if(stack1.getItem() instanceof BackpackItem && stack1.has(BackpackItem.INVENTORY_ID)
                    && stack1.get(BackpackItem.INVENTORY_ID) == index){
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

        if(stack.getItem() instanceof BlockItem && ((BlockItem)stack.getItem()).getBlock() instanceof ShulkerBoxBlock && stack.has(DataComponents.CONTAINER)){
            ItemContainerContents container = stack.get(DataComponents.CONTAINER);
            // Check whether the container contains a backpack
            //noinspection RedundantIfStatement
            if(container != null && container.stream().anyMatch(stack1 -> stack1.getItem() instanceof BackpackItem))
                return false;
        }
        return true;
    }

    private static boolean canStack(ItemStack stack1, ItemStack stack2){
        return ItemStack.isSameItemSameComponents(stack1, stack2);
    }

    public void save(Path path, HolderLookup.Provider provider){
        CompoundTag compound = new CompoundTag();
        compound.putInt("stacks", this.stacks.size());
        for(int slot = 0; slot < this.stacks.size(); slot++)
            if(!this.stacks.get(slot).isEmpty())
                compound.put("stack" + slot, ItemStack.CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), this.stacks.get(slot)).getOrThrow());
        compound.putIntArray("bagsInThisBag", this.bagsInThisBag.stream().mapToInt(Integer::intValue).toArray());
        compound.putIntArray("bagsThisBagIsIn", this.bagsThisBagIsIn.stream().mapToInt(Integer::intValue).toArray());
        compound.putInt("layer", this.layer);
        // Add the data version the file was written in, so it can be used by DataFixerUpper when loading backpack inventories
        compound.putInt("version", SharedConstants.getCurrentVersion().dataVersion().version());
        try{
            NbtIo.write(compound, path);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void load(Path path, HolderLookup.Provider provider){
        CompoundTag compound;
        try{
            compound = NbtIo.read(path);
        }catch(Exception e){
            e.printStackTrace();
            return;
        }
        // Obtain data version the inventory was written in, if not present, assume Minecraft 1.20.4
        int dataVersion = compound.getIntOr("version", 3700);
        this.stacks.clear();
        int size = compound.getInt("stacks").or(() -> compound.getInt("rows").map(i -> i * 9)).or(() -> compound.getInt("slots")).orElse(0); // Do this for compatibility with older versions
        for(int slot = 0; slot < size; slot++){
            ItemStack stack = compound.getCompound("stack" + slot)
                .map(tag -> (CompoundTag)DataFixers.getDataFixer().update(References.ITEM_STACK, new Dynamic<>(provider.createSerializationContext(NbtOps.INSTANCE), tag), dataVersion, SharedConstants.getCurrentVersion().dataVersion().version()).getValue())
                .map(tag -> ItemStack.CODEC.decode(provider.createSerializationContext(NbtOps.INSTANCE), tag))
                .filter(DataResult::isSuccess)
                .map(result -> result.getOrThrow().getFirst())
                .orElse(ItemStack.EMPTY);
            this.stacks.add(stack);
        }
        this.bagsInThisBag.clear();
        Arrays.stream(compound.getIntArray("bagsInThisBag").orElseGet(() -> new int[0])).forEach(this.bagsInThisBag::add);
        this.bagsThisBagIsIn.clear();
        Arrays.stream(compound.getIntArray("bagsThisBagIsIn").orElseGet(() -> new int[0])).forEach(this.bagsThisBagIsIn::add);
        this.layer = compound.getIntOr("layer", 0);
    }

    public void setStackInSlot(int slot, ItemStack stack){
        ItemStack oldStack = this.stacks.get(slot);
        this.stacks.set(slot, ItemStack.EMPTY);

        if(!this.remote && oldStack.getItem() instanceof BackpackItem && oldStack.has(BackpackItem.INVENTORY_ID)){
            //noinspection DataFlowIssue
            int index = oldStack.get(BackpackItem.INVENTORY_ID);
            boolean contains = false;
            for(ItemStack stack1 : this.stacks){
                //noinspection DataFlowIssue
                if(stack1.getItem() instanceof BackpackItem && stack1.has(BackpackItem.INVENTORY_ID)
                    && stack1.get(BackpackItem.INVENTORY_ID) == index){
                    contains = true;
                    break;
                }
            }
            if(!contains)
                BackpackStorageManager.onExtract(index, this.inventoryIndex);
        }

        this.stacks.set(slot, stack);

        if(!this.remote && stack.getItem() instanceof BackpackItem && stack.has(BackpackItem.INVENTORY_ID)){
            //noinspection DataFlowIssue
            int index = stack.get(BackpackItem.INVENTORY_ID);
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
        if(!bag.has(BackpackItem.INVENTORY_ID))
            return true;
        //noinspection DataFlowIssue
        int index = bag.get(BackpackItem.INVENTORY_ID);
        return index != this.inventoryIndex && !this.bagsThisBagIsIn.contains(index);
    }
}
