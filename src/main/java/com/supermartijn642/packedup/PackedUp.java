package com.supermartijn642.packedup;

import com.supermartijn642.core.gui.BaseContainerType;
import com.supermartijn642.core.item.CreativeItemGroup;
import com.supermartijn642.core.network.PacketChannel;
import com.supermartijn642.core.registry.GeneratorRegistrationHandler;
import com.supermartijn642.core.registry.RegistrationHandler;
import com.supermartijn642.core.registry.RegistryEntryAcceptor;
import com.supermartijn642.packedup.compat.Compatibility;
import com.supermartijn642.packedup.generators.*;
import com.supermartijn642.packedup.packets.PacketOpenBag;
import com.supermartijn642.packedup.packets.PacketRename;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.imc.CurioIMCMessage;

import java.util.HashSet;
import java.util.Set;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
@Mod("packedup")
public class PackedUp {

    public static final PacketChannel CHANNEL = PacketChannel.create("packedup");

    @RegistryEntryAcceptor(namespace = "packedup", identifier = "container", registry = RegistryEntryAcceptor.Registry.MENU_TYPES)
    public static BaseContainerType<BackpackContainer> container;

    @RegistryEntryAcceptor(namespace = "packedup", identifier = "basicbackpack", registry = RegistryEntryAcceptor.Registry.ITEMS)
    public static BackpackItem basicbackpack;
    @RegistryEntryAcceptor(namespace = "packedup", identifier = "ironbackpack", registry = RegistryEntryAcceptor.Registry.ITEMS)
    public static BackpackItem ironbackpack;
    @RegistryEntryAcceptor(namespace = "packedup", identifier = "copperbackpack", registry = RegistryEntryAcceptor.Registry.ITEMS)
    public static BackpackItem copperbackpack;
    @RegistryEntryAcceptor(namespace = "packedup", identifier = "silverbackpack", registry = RegistryEntryAcceptor.Registry.ITEMS)
    public static BackpackItem silverbackpack;
    @RegistryEntryAcceptor(namespace = "packedup", identifier = "goldbackpack", registry = RegistryEntryAcceptor.Registry.ITEMS)
    public static BackpackItem goldbackpack;
    @RegistryEntryAcceptor(namespace = "packedup", identifier = "diamondbackpack", registry = RegistryEntryAcceptor.Registry.ITEMS)
    public static BackpackItem diamondbackpack;
    @RegistryEntryAcceptor(namespace = "packedup", identifier = "obsidianbackpack", registry = RegistryEntryAcceptor.Registry.ITEMS)
    public static BackpackItem obsidianbackpack;

    public static final CreativeItemGroup ITEM_GROUP = CreativeItemGroup.create("packedup", () -> basicbackpack);

    public PackedUp(){
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::interModEnqueue);

        CHANNEL.registerMessage(PacketRename.class, PacketRename::new, true);
        CHANNEL.registerMessage(PacketOpenBag.class, PacketOpenBag::new, true);

        register();
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> PackedUpClient::register);
        registerGenerators();
    }

    public void init(FMLCommonSetupEvent e){
        Compatibility.init();
    }

    public void interModEnqueue(InterModEnqueueEvent e){
        InterModComms.sendTo("curios", CuriosAPI.IMC.REGISTER_TYPE, () -> new CurioIMCMessage("back").setSize(1).setEnabled(true));
    }

    private static void register(){
        RegistrationHandler handler = RegistrationHandler.get("packedup");

        // Backpack items
        for(BackpackType type : BackpackType.values())
            handler.registerItem(type.getRegistryName(), () -> new BackpackItem(type));
        // Container
        handler.registerMenuType("container", () -> BaseContainerType.create(
            (container, data) -> {
                data.writeInt(container.type.ordinal());
                data.writeInt(container.bagSlot);
                data.writeUtf(ITextComponent.Serializer.toJson(container.bagName));
                BackpackInventory inventory = container.inventory;
                data.writeInt(inventory.getInventoryIndex());
                data.writeInt(inventory.bagsInThisBag.size());
                inventory.bagsInThisBag.forEach(data::writeInt);
                data.writeInt(inventory.bagsThisBagIsIn.size());
                inventory.bagsThisBagIsIn.forEach(data::writeInt);
                data.writeInt(inventory.layer);
            },
            (player, data) -> {
                BackpackType type = BackpackType.values()[data.readInt()];
                int bagSlot = data.readInt();
                ITextComponent bagName = ITextComponent.Serializer.fromJson(data.readUtf());
                int inventoryIndex = data.readInt();
                int size = data.readInt();
                Set<Integer> bagsInThisBag = new HashSet<>(size);
                for(int i = 0; i < size; i++)
                    bagsInThisBag.add(data.readInt());
                size = data.readInt();
                Set<Integer> bagsThisBagIsIn = new HashSet<>(size);
                for(int i = 0; i < size; i++)
                    bagsThisBagIsIn.add(data.readInt());
                int layer = data.readInt();
                return new BackpackContainer(player, bagSlot, bagName, inventoryIndex, type, bagsInThisBag, bagsThisBagIsIn, layer);
            }
        ));
        // Backpack upgrade recipe
        handler.registerRecipeSerializer("upgrade_backpack", BackpackUpgradeRecipe.SERIALIZER);
        // Backpack enabled recipe condition
        handler.registerResourceConditionSerializer("is_backpack_enabled", BackpackRecipeCondition.SERIALIZER);
    }

    private static void registerGenerators(){
        GeneratorRegistrationHandler handler = GeneratorRegistrationHandler.get("packedup");
        // Register all the generators
        handler.addGenerator(PackedUpAdvancementGenerator::new);
        handler.addGenerator(PackedUpLanguageGenerator::new);
        handler.addGenerator(PackedUpModelGenerator::new);
        handler.addGenerator(PackedUpRecipeGenerator::new);
        handler.addGenerator(PackedUpTagGenerator::new);
    }
}
