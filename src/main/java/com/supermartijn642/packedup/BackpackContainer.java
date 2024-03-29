package com.supermartijn642.packedup;

import com.supermartijn642.core.gui.BaseContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Set;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public class BackpackContainer extends BaseContainer {

    public final BackpackType type;
    public final int bagSlot;
    public final ITextComponent bagName;
    public final BackpackInventory inventory;

    public BackpackContainer(PlayerEntity player, int bagSlot, ITextComponent bagName, int inventoryIndex, BackpackType type, Set<Integer> bagsInThisBag, Set<Integer> bagsThisBagIsIn, int layer){
        super(PackedUp.container, player);
        this.bagSlot = bagSlot;
        this.bagName = bagName;
        this.type = type;

        this.inventory = player.level.isClientSide ?
            new BackpackInventory(true, inventoryIndex, type.getSlots(), bagsInThisBag, bagsThisBagIsIn, layer) :
            BackpackStorageManager.getInventory(inventoryIndex);

        this.addSlots();
    }

    @Override
    protected void addSlots(PlayerEntity player){
        // Backpack slots
        int startX = Math.max(0, 9 - this.type.getColumns()) * 18 / 2 + 8;
        int startY = 17;
        for(int row = 0; row < this.type.getRows(); row++){
            for(int column = 0; column < this.type.getColumns(); column++){
                int x = startX + 18 * column, y = startY + 18 * row, index = row * this.type.getColumns() + column;
                this.addSlot(new SlotItemHandler(inventory, index, x, y));
            }
        }

        // Player slots
        startX = Math.max(0, this.type.getColumns() - 9) * 18 / 2 + 8;
        startY += this.type.getRows() * 18 + 13;
        this.addPlayerSlots(startX, startY);
    }

    @Override
    protected void addPlayerSlots(int x, int y){
        // player
        for(int row = 0; row < 3; row++){
            for(int column = 0; column < 9; column++){
                if(this.bagSlot == row * 9 + column + 9)
                    this.addSlot(new Slot(this.player.inventory, row * 9 + column + 9, x + 18 * column, y + 18 * row) {
                        @Override
                        public boolean mayPickup(PlayerEntity player){
                            return false;
                        }
                    });
                else
                    this.addSlot(new Slot(this.player.inventory, row * 9 + column + 9, x + 18 * column, y + 18 * row));
            }
        }

        // hot bar
        for(int column = 0; column < 9; column++){
            if(this.bagSlot == column)
                this.addSlot(new Slot(this.player.inventory, column, x + 18 * column, y + 58) {
                    @Override
                    public boolean mayPickup(PlayerEntity player){
                        return false;
                    }
                });
            else
                this.addSlot(new Slot(this.player.inventory, column, x + 18 * column, y + 58));
        }
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index){
        ItemStack returnStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if(slot.hasItem()){
            ItemStack slotStack = slot.getItem();
            returnStack = slotStack.copy();
            if(index < this.type.getSlots()){
                if(!this.moveItemStackTo(slotStack, this.type.getSlots(), this.slots.size(), true))
                    return ItemStack.EMPTY;
            }else if(!this.moveItemStackTo(slotStack, 0, this.type.getSlots(), false))
                return ItemStack.EMPTY;

            if(slotStack.isEmpty())
                slot.set(ItemStack.EMPTY);
            else
                slot.setChanged();
        }

        return returnStack;
    }

    @Override
    public ItemStack clicked(int index, int dragType, ClickType clickType, PlayerEntity player){
        if(clickType == ClickType.SWAP && dragType == this.bagSlot)
            return ItemStack.EMPTY;
        if(clickType == ClickType.PICKUP && dragType == 1 && index >= 0){
            ItemStack stack = this.getSlot(index).getItem();
            if(stack.getItem() instanceof BackpackItem){
                int playerSlot = index - this.type.getSlots();
                if(playerSlot >= 0)
                    playerSlot += playerSlot < 27 ? 9 : -27;
                if(!player.level.isClientSide)
                    PackedUpCommon.openBackpackInventory(stack, player, playerSlot < 0 ? this.bagSlot : playerSlot);
                return ItemStack.EMPTY;
            }
        }
        return super.clicked(index, dragType, clickType, player);
    }
}
