package com.supermartijn642.packedup.screen;

import com.supermartijn642.packedup.storage.BackpackInventory;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

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

    private static class BackpackInventoryAsContainer implements Container {

        private final BackpackInventory inventory;

        private BackpackInventoryAsContainer(BackpackInventory inventory){
            this.inventory = inventory;
        }

        @Override
        public void clearContent(){
            int size = this.inventory.getStacks().size();
            for(int i = 0; i < size; i++)
                this.inventory.setStackInSlot(i, ItemStack.EMPTY);
        }

        @Override
        public int getContainerSize(){
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
        public ItemStack getItem(int slot){
            return this.inventory.getStackInSlot(slot);
        }

        @Override
        public ItemStack removeItem(int slot, int count){
            return this.inventory.extractItem(slot, count);
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot){
            ItemStack stack = this.getItem(slot);
            return this.removeItem(slot, stack.getCount());
        }

        @Override
        public void setItem(int slot, ItemStack stack){
            this.inventory.setStackInSlot(slot, stack);
        }

        @Override
        public int getMaxStackSize(){
            return Integer.MAX_VALUE;
        }

        @Override
        public int getMaxStackSize(ItemStack stack){
            return stack.isEmpty() ? this.getMaxStackSize() : stack.getCount();
        }

        @Override
        public void setChanged(){
        }

        @Override
        public boolean stillValid(Player player){
            return true;
        }

        @Override
        public void startOpen(ContainerUser user){
            throw new IllegalStateException("This should not be called for a slot container!");
        }

        @Override
        public void stopOpen(ContainerUser user){
            throw new IllegalStateException("This should not be called for a slot container!");
        }

        @Override
        public List<ContainerUser> getEntitiesWithContainerOpen(){
            throw new IllegalStateException("This should not be called for a slot container!");
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack stack){
            return this.inventory.isItemValid(stack);
        }

        @Override
        public boolean canTakeItem(Container into, int slot, ItemStack stack){
            return true;
        }
    }
}
