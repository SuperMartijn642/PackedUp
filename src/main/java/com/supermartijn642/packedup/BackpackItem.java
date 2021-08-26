package com.supermartijn642.packedup;

import com.supermartijn642.core.TextComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public class BackpackItem extends Item {

    public BackpackType type;

    public BackpackItem(BackpackType type){
        super(type.isEnabled() ? new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_SEARCH) : new Item.Properties().stacksTo(1));
        this.type = type;
        this.setRegistryName(type.getRegistryName());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn){
        ItemStack stack = playerIn.getItemInHand(handIn);
        if(!playerIn.isCrouching()){
            if(!worldIn.isClientSide && stack.getItem() instanceof BackpackItem){
                int bagSlot = handIn == InteractionHand.MAIN_HAND ? playerIn.getInventory().selected : -1;
                CommonProxy.openBackpackInventory(stack, playerIn, bagSlot);
            }
        }else if(worldIn.isClientSide)
            ClientProxy.openScreen(TextComponents.item(stack.getItem()).format(), TextComponents.itemStack(stack).format());
        return InteractionResultHolder.sidedSuccess(stack, worldIn.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
        tooltip.add(TextComponents.translation("packedup.backpacks.info.one", TextComponents.string(Integer.toString(this.type.getSlots())).color(ChatFormatting.GOLD).get()).color(ChatFormatting.AQUA).get());
        Component key = ClientProxy.getKeyBindCharacter();
        if(key != null)
            tooltip.add(TextComponents.translation("packedup.backpacks.info.two", key).color(ChatFormatting.AQUA).get());

        if(flagIn.isAdvanced()){
            CompoundTag compound = stack.getOrCreateTag();
            if(compound.contains("packedup:invIndex"))
                tooltip.add(TextComponents.translation("packedup.backpacks.info.inventory_index", compound.getInt("packedup:invIndex")).get());
        }

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items){
        if(this.type.isEnabled())
            super.fillItemCategory(group, items);
    }

    @Override
    public boolean isFireResistant(){
        return !PUConfig.canBackpacksBurn.get();
    }

    public static class ContainerProvider implements MenuProvider {

        private final int inventoryIndex;
        private final Component displayName;
        private final int bagSlot;
        private final BackpackInventory inventory;
        private final BackpackType type;

        public ContainerProvider(Component displayName, int bagSlot, int inventoryIndex, BackpackInventory inventory, BackpackType type){
            this.inventoryIndex = inventoryIndex;
            this.displayName = displayName;
            this.bagSlot = bagSlot;
            this.inventory = inventory;
            this.type = type;
        }

        @Override
        public Component getDisplayName(){
            return this.displayName;
        }

        @Nullable
        @Override
        public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
            return new BackpackContainer(id, playerInv, this.bagSlot, this.inventoryIndex, this.type, this.inventory.bagsInThisBag, this.inventory.bagsThisBagIsIn, this.inventory.layer);
        }
    }
}
