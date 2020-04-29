package com.supermartijn642.packedup;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

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
        if(!playerIn.isShiftKeyDown()){
            if(!worldIn.isRemote){
                CompoundNBT compound = stack.getOrCreateTag();
                if(!compound.contains("packedup:invIndex")){
                    compound.putInt("packedup:invIndex", BackpackStorageManager.createInventoryIndex(this.type));
                    stack.setTag(compound);
                }else{
                    BackpackInventory inventory = BackpackStorageManager.getInventory(compound.getInt("packedup:invIndex"));
                    int rows = inventory.getSlots() / 9;
                    if(rows != this.type.getRows())
                        inventory.adjustSize(this.type.getRows() * 9);
                }
                int inventoryIndex = compound.getInt("packedup:invIndex");
                int bagSlot = handIn == Hand.MAIN_HAND ? playerIn.inventory.currentItem : -1;
                NetworkHooks.openGui((ServerPlayerEntity)playerIn, new ContainerProvider(inventoryIndex, stack.getDisplayName(), bagSlot), a -> a.writeInt(this.type.getRows()).writeInt(bagSlot));
            }
        }
        else if(worldIn.isRemote){
            ClientProxy.openScreen(stack.getItem().getDisplayName(stack).getFormattedText(), stack.getDisplayName().getFormattedText());
        }
        return ActionResult.resultSuccess(stack);
    }

    private static class ContainerProvider implements INamedContainerProvider {
        private int inventoryIndex;
        private ITextComponent displayName;
        private int bagSlot;

        private ContainerProvider(int inventoryIndex, ITextComponent displayName, int bagSlot){
            this.inventoryIndex = inventoryIndex;
            this.displayName = displayName;
            this.bagSlot = bagSlot;
        }

        @Override
        public ITextComponent getDisplayName(){
            return this.displayName;
        }

        @Nullable
        @Override
        public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player){
            return new BackpackContainer(id, this.inventoryIndex, inventory, this.bagSlot);
        }
    }
}
