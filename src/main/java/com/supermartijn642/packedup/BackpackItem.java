package com.supermartijn642.packedup;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public class BackpackItem extends Item {

    public BackpackType type;

    public BackpackItem(BackpackType type){
        super(type.isEnabled() ? new Item.Properties().maxStackSize(1).group(ItemGroup.SEARCH) : new Item.Properties().maxStackSize(1));
        this.type = type;
        this.setRegistryName(type.getRegistryName());
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn){
        ItemStack stack = playerIn.getHeldItem(handIn);
        if(!playerIn.isSneaking()){
            if(!worldIn.isRemote && stack.getItem() instanceof BackpackItem){
                int bagSlot = handIn == Hand.MAIN_HAND ? playerIn.inventory.currentItem : -1;
                CommonProxy.openBackpackInventory(stack, playerIn, bagSlot);
            }
        }else if(worldIn.isRemote){
            ClientProxy.openScreen(stack.getItem().getDisplayName(stack).getString(), stack.getDisplayName().getString());
        }
        return ActionResult.resultSuccess(stack);
    }

    public static class ContainerProvider implements INamedContainerProvider {
        private int inventoryIndex;
        private ITextComponent displayName;
        private int bagSlot;
        private BackpackInventory inventory;

        public ContainerProvider(ITextComponent displayName, int bagSlot, int inventoryIndex, BackpackInventory inventory){
            this.inventoryIndex = inventoryIndex;
            this.displayName = displayName;
            this.bagSlot = bagSlot;
            this.inventory = inventory;
        }

        @Override
        public ITextComponent getDisplayName(){
            return this.displayName;
        }

        @Nullable
        @Override
        public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player){
            return new BackpackContainer(id, playerInv, this.bagSlot, this.inventoryIndex, this.inventory.rows, this.inventory.bagsInThisBag, this.inventory.bagsThisBagIsIn, this.inventory.layer);
        }
    }
}
