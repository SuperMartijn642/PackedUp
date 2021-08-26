package com.supermartijn642.packedup;

import com.supermartijn642.core.network.PacketChannel;
import com.supermartijn642.packedup.integration.BaublesActive;
import com.supermartijn642.packedup.integration.BaublesInactive;
import com.supermartijn642.packedup.packets.PacketBackpackContainer;
import com.supermartijn642.packedup.packets.PacketOpenBag;
import com.supermartijn642.packedup.packets.PacketRename;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created 4/8/2020 by SuperMartijn642
 */
@Mod(modid = PackedUp.MODID, name = PackedUp.NAME, version = PackedUp.VERSION, dependencies = PackedUp.DEPENDENCIES)
public class PackedUp {

    public static final String MODID = "packedup";
    public static final String NAME = "Packed Up";
    public static final String VERSION = "1.0.23a";
    public static final String DEPENDENCIES = "required-after:supermartijn642corelib@[1.0.12,);required-after:supermartijn642configlib@[1.0.9,)";

    @Mod.Instance
    public static PackedUp instance;

    public static final PacketChannel CHANNEL = PacketChannel.create("packedup");

    public static BaublesInactive baubles;

    @GameRegistry.ObjectHolder("packedup:basicbackpack")
    public static BackpackItem basicbackpack;
    @GameRegistry.ObjectHolder("packedup:ironbackpack")
    public static BackpackItem ironbackpack;
    @GameRegistry.ObjectHolder("packedup:copperbackpack")
    public static BackpackItem copperbackpack;
    @GameRegistry.ObjectHolder("packedup:silverbackpack")
    public static BackpackItem silverbackpack;
    @GameRegistry.ObjectHolder("packedup:goldbackpack")
    public static BackpackItem goldbackpack;
    @GameRegistry.ObjectHolder("packedup:diamondbackpack")
    public static BackpackItem diamondbackpack;
    @GameRegistry.ObjectHolder("packedup:obsidianbackpack")
    public static BackpackItem obsidianbackpack;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e){
        CHANNEL.registerMessage(PacketRename.class, PacketRename::new, true);
        CHANNEL.registerMessage(PacketOpenBag.class, PacketOpenBag::new, true);
        CHANNEL.registerMessage(PacketBackpackContainer.class, PacketBackpackContainer::new, true);

        baubles = Loader.isModLoaded("baubles") ? new BaublesActive() : new BaublesInactive();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e){
        MinecraftForge.EVENT_BUS.register(BackpackStorageManager.class);
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
            ClientProxy.init();
    }

    @Mod.EventBusSubscriber
    public static class RegistryEvents {

        @SubscribeEvent
        public static void onItemRegistry(final RegistryEvent.Register<Item> e){
            for(BackpackType type : BackpackType.values())
                e.getRegistry().register(new BackpackItem(type));
        }
    }

}
