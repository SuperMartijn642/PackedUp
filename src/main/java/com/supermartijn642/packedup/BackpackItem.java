package com.supermartijn642.packedup;

import com.supermartijn642.core.TextComponents;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public class BackpackItem extends Item {

    public BackpackType type;

    public BackpackItem(BackpackType type){
        super(type.isEnabled() ? new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_SEARCH) : new Item.Properties().stacksTo(1));
        this.type = type;
        this.setRegistryName(type.getRegistryName());
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn){
        ItemStack stack = playerIn.getItemInHand(handIn);
        if(!playerIn.isCrouching()){
            if(!worldIn.isClientSide && stack.getItem() instanceof BackpackItem){
                int bagSlot = handIn == Hand.MAIN_HAND ? playerIn.inventory.selected : -1;
                CommonProxy.openBackpackInventory(stack, playerIn, bagSlot);
            }
        }else if(worldIn.isClientSide)
            ClientProxy.openScreen(TextComponents.item(stack.getItem()).format(), TextComponents.itemStack(stack).format());
        return ActionResult.sidedSuccess(stack, worldIn.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
        tooltip.add(new TranslationTextComponent("packedup.backpacks.info.one", this.type.getRows() * 9).withStyle(TextFormatting.AQUA));
        ITextComponent key = ClientProxy.getKeyBindCharacter();
        if(key != null)
            tooltip.add(new TranslationTextComponent("packedup.backpacks.info.two", key).withStyle(TextFormatting.AQUA));

        if(flagIn.isAdvanced()){
            CompoundNBT compound = stack.getOrCreateTag();
            if(compound.contains("packedup:invIndex"))
                tooltip.add(new TranslationTextComponent("packedup.backpacks.info.inventory_index", compound.getInt("packedup:invIndex")));
        }

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items){
        if(this.type.isEnabled())
            super.fillItemCategory(group, items);
    }

    public static class ContainerProvider implements INamedContainerProvider {
        private final int inventoryIndex;
        private final ITextComponent displayName;
        private final int bagSlot;
        private final BackpackInventory inventory;
        private final BackpackType type;

        public ContainerProvider(ITextComponent displayName, int bagSlot, int inventoryIndex, BackpackInventory inventory, BackpackType type){
            this.inventoryIndex = inventoryIndex;
            this.displayName = displayName;
            this.bagSlot = bagSlot;
            this.inventory = inventory;
            this.type = type;
        }

        @Override
        public ITextComponent getDisplayName(){
            return this.displayName;
        }

        @Nullable
        @Override
        public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player){
            return new BackpackContainer(id, playerInv, this.bagSlot, this.inventoryIndex, this.type, this.inventory.bagsInThisBag, this.inventory.bagsThisBagIsIn, this.inventory.layer);
        }
    }
}
