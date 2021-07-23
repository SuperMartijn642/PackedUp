package com.supermartijn642.packedup;

import com.supermartijn642.core.gui.BaseContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Set;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public class BackpackContainer extends BaseContainer {

    public final BackpackType type;
    public final int bagSlot;

    public BackpackContainer(int id, Inventory player, int bagSlot, int inventoryIndex, BackpackType type, Set<Integer> bagsInThisBag, Set<Integer> bagsThisBagIsIn, int layer){
        super(PackedUp.container, id, player.player);
        this.bagSlot = bagSlot;
        this.type = type;

        BackpackInventory inventory = player.player.level.isClientSide ?
            new BackpackInventory(true, inventoryIndex, type.getSlots(), bagsInThisBag, bagsThisBagIsIn, layer) :
            BackpackStorageManager.getInventory(inventoryIndex);

        this.addSlots(inventory, player);
    }

    private void addSlots(IItemHandler inventory, Inventory player){
        int startX = Math.max(0, 9 - this.type.getColumns()) * 18 / 2 + 8;
        int startY = 17;

        for(int row = 0; row < this.type.getRows(); row++){
            for(int column = 0; column < this.type.getColumns(); column++){
                int x = startX + 18 * column, y = startY + 18 * row, index = row * this.type.getColumns() + column;
                this.addSlot(new SlotItemHandler(inventory, index, x, y));
            }
        }

        startX = Math.max(0, this.type.getColumns() - 9) * 18 / 2 + 8;
        startY += this.type.getRows() * 18 + 13;

        // player slots
        for(int row = 0; row < 3; row++){
            for(int column = 0; column < 9; column++){
                int x = startX + 18 * column, y = startY + 18 * row, index = row * 9 + column + 9;
                if(index == this.bagSlot)
                    this.addSlot(new Slot(player, index, x, y) {
                        public boolean canTakeStack(Player playerIn){
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
                    public boolean canTakeStack(Player player){
                        return false;
                    }
                });
            else
                this.addSlot(new Slot(player, column, x, y));
        }
    }

    @Override
    protected void addSlots(Player playerEntity){
    }

    @Override
    public boolean stillValid(Player player){
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index){
        ItemStack returnStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if(slot != null && slot.hasItem()){
            ItemStack slotStack = slot.getItem();
            returnStack = slotStack.copy();
            if(index < this.type.getSlots()){
                if(!this.moveItemStackTo(slotStack, this.type.getSlots(), this.slots.size(), true)){
                    return ItemStack.EMPTY;
                }
            }else if(!this.moveItemStackTo(slotStack, 0, this.type.getSlots(), false)){
                return ItemStack.EMPTY;
            }

            if(slotStack.isEmpty()){
                slot.set(ItemStack.EMPTY);
            }else{
                slot.setChanged();
            }
        }

        return returnStack;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player){
        if(clickTypeIn == ClickType.SWAP && dragType == this.bagSlot)
            return;
        if(clickTypeIn == ClickType.PICKUP && dragType == 1 && slotId >= 0){
            Slot slot = this.getSlot(slotId);
            if(slot.mayPickup(player)){
                ItemStack stack = slot.getItem();
                if(stack.getItem() instanceof BackpackItem){
                    if(!player.level.isClientSide){
                        int bagSlot = slotId >= this.type.getSlots() + 27 ? slotId - this.type.getSlots() - 27 : slotId >= this.type.getSlots() ? slotId - this.type.getSlots() - 9 : -1;
                        CommonProxy.openBackpackInventory(stack, player, bagSlot);
                    }
                    return;
                }
            }
        }
        super.clicked(slotId, dragType, clickTypeIn, player);
    }
}
