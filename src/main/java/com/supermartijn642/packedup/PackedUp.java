package com.supermartijn642.packedup;

import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.ObjectHolder;

import java.util.HashSet;
import java.util.Set;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
@Mod("packedup")
public class PackedUp {

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation("packedup", "main"), () -> "1", "1"::equals, "1"::equals);

    public static CommonProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new CommonProxy());

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
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, PUConfig.CONFIG_SPEC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
        MinecraftForge.EVENT_BUS.register(BackpackStorageManager.class);

        CHANNEL.registerMessage(0, PacketRename.class, PacketRename::encode, PacketRename::decode, PacketRename::handle);
        CHANNEL.registerMessage(1, PacketMaxLayers.class, PacketMaxLayers::encode, PacketMaxLayers::decode, PacketMaxLayers::handle);
    }

    public void init(FMLCommonSetupEvent e){
        proxy.init();
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
                int rows = data.readInt();
                int size = data.readInt();
                Set<Integer> bagsInThisBag = new HashSet<>(size);
                for(int i = 0; i < size; i++)
                    bagsInThisBag.add(data.readInt());
                size = data.readInt();
                Set<Integer> bagsThisBagIsIn = new HashSet<>(size);
                for(int i = 0; i < size; i++)
                    bagsThisBagIsIn.add(data.readInt());
                int layer = data.readInt();
                return new BackpackContainer(windowId, inv, bagSlot, inventoryIndex, rows, bagsInThisBag, bagsThisBagIsIn, layer);
            }).setRegistryName("container"));
        }

        @SubscribeEvent
        public static void onRecipeRegistry(final RegistryEvent.Register<IRecipeSerializer<?>> e){
            e.getRegistry().register(BACKPACK_RECIPE_SERIALIZER.setRegistryName(new ResourceLocation("packedup", "backpackrecipe")));
        }
    }

}
