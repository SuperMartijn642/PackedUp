package com.supermartijn642.packedup;

import com.supermartijn642.core.network.PacketChannel;
import com.supermartijn642.packedup.compat.Compatibility;
import com.supermartijn642.packedup.packets.PacketOpenBag;
import com.supermartijn642.packedup.packets.PacketRename;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;
import top.theillusivec4.curios.api.SlotTypeMessage;

import java.util.HashSet;
import java.util.Set;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
@Mod("packedup")
public class PackedUp {

    public static final PacketChannel CHANNEL = PacketChannel.create("packedup");

    @ObjectHolder("packedup:container")
    public static ContainerType<BackpackContainer> container;

    @ObjectHolder("packedup:basicbackpack")
    public static BackpackItem basicbackpack;
    @ObjectHolder("packedup:ironbackpack")
    public static BackpackItem ironbackpack;
    @ObjectHolder("packedup:copperbackpack")
    public static BackpackItem copperbackpack;
    @ObjectHolder("packedup:silverbackpack")
    public static BackpackItem silverbackpack;
    @ObjectHolder("packedup:goldbackpack")
    public static BackpackItem goldbackpack;
    @ObjectHolder("packedup:diamondbackpack")
    public static BackpackItem diamondbackpack;
    @ObjectHolder("packedup:obsidianbackpack")
    public static BackpackItem obsidianbackpack;

    public static final IRecipeSerializer<BackpackRecipe> BACKPACK_RECIPE_SERIALIZER = new BackpackRecipe.Serializer();

    public PackedUp(){
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::interModEnqueue);

        CHANNEL.registerMessage(PacketRename.class, PacketRename::new, true);
        CHANNEL.registerMessage(PacketOpenBag.class, PacketOpenBag::new, true);
    }

    public void init(FMLCommonSetupEvent e){
        Compatibility.init();
    }

    public void interModEnqueue(InterModEnqueueEvent e){
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("back").size(1).build());
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void onItemRegistry(final RegistryEvent.Register<Item> e){
            for(BackpackType type : BackpackType.values())
                e.getRegistry().register(new BackpackItem(type));
        }

        @SubscribeEvent
        public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> e){
            e.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                int bagSlot = data.readInt();
                int inventoryIndex = data.readInt();
                BackpackType type = BackpackType.values()[data.readInt()];
                int size = data.readInt();
                Set<Integer> bagsInThisBag = new HashSet<>(size);
                for(int i = 0; i < size; i++)
                    bagsInThisBag.add(data.readInt());
                size = data.readInt();
                Set<Integer> bagsThisBagIsIn = new HashSet<>(size);
                for(int i = 0; i < size; i++)
                    bagsThisBagIsIn.add(data.readInt());
                int layer = data.readInt();
                return new BackpackContainer(windowId, inv, bagSlot, inventoryIndex, type, bagsInThisBag, bagsThisBagIsIn, layer);
            }).setRegistryName("container"));
        }

        @SubscribeEvent
        public static void onRecipeRegistry(final RegistryEvent.Register<IRecipeSerializer<?>> e){
            CraftingHelper.register(BackpackRecipeCondition.SERIALIZER);
            e.getRegistry().register(BACKPACK_RECIPE_SERIALIZER.setRegistryName(new ResourceLocation("packedup", "backpackrecipe")));
        }
    }

}
