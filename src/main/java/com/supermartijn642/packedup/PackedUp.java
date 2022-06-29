package com.supermartijn642.packedup;

import com.supermartijn642.core.network.PacketChannel;
import com.supermartijn642.packedup.compat.Compatibility;
import com.supermartijn642.packedup.packets.PacketOpenBag;
import com.supermartijn642.packedup.packets.PacketRename;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegisterEvent;
import top.theillusivec4.curios.api.SlotTypeMessage;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
@Mod("packedup")
public class PackedUp {

    public static final PacketChannel CHANNEL = PacketChannel.create("packedup");

    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab("packedup") {
        @Override
        public ItemStack makeIcon(){
            return new ItemStack(basicbackpack);
        }
    };

    @ObjectHolder(value = "packedup:container", registryName = "minecraft:menu")
    public static MenuType<BackpackContainer> container;

    @ObjectHolder(value = "packedup:basicbackpack", registryName = "minecraft:item")
    public static BackpackItem basicbackpack;
    @ObjectHolder(value = "packedup:ironbackpack", registryName = "minecraft:item")
    public static BackpackItem ironbackpack;
    @ObjectHolder(value = "packedup:copperbackpack", registryName = "minecraft:item")
    public static BackpackItem copperbackpack;
    @ObjectHolder(value = "packedup:silverbackpack", registryName = "minecraft:item")
    public static BackpackItem silverbackpack;
    @ObjectHolder(value = "packedup:goldbackpack", registryName = "minecraft:item")
    public static BackpackItem goldbackpack;
    @ObjectHolder(value = "packedup:diamondbackpack", registryName = "minecraft:item")
    public static BackpackItem diamondbackpack;
    @ObjectHolder(value = "packedup:obsidianbackpack", registryName = "minecraft:item")
    public static BackpackItem obsidianbackpack;

    public static final RecipeSerializer<BackpackRecipe> BACKPACK_RECIPE_SERIALIZER = new BackpackRecipe.Serializer();

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
        public static void onRegisterEvent(RegisterEvent e){
            if(e.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS))
                onItemRegistry(Objects.requireNonNull(e.getForgeRegistry()));
            else if(e.getRegistryKey().equals(ForgeRegistries.Keys.CONTAINER_TYPES))
                onContainerRegistry(Objects.requireNonNull(e.getForgeRegistry()));
            else if(e.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS))
                onRecipeRegistry(Objects.requireNonNull(e.getForgeRegistry()));
        }

        public static void onItemRegistry(IForgeRegistry<Item> registry){
            for(BackpackType type : BackpackType.values())
                registry.register(type.getRegistryName(), new BackpackItem(type));
        }

        public static void onContainerRegistry(IForgeRegistry<MenuType<?>> registry){
            registry.register("container", IForgeMenuType.create((windowId, inv, data) -> {
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
            }));
        }

        public static void onRecipeRegistry(IForgeRegistry<RecipeSerializer<?>> registry){
            CraftingHelper.register(BackpackRecipeCondition.SERIALIZER);
            registry.register("backpackrecipe", BACKPACK_RECIPE_SERIALIZER);
        }
    }

}
