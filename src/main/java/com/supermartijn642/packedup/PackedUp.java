package com.supermartijn642.packedup;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created 4/8/2020 by SuperMartijn642
 */
@Mod(modid = PackedUp.MODID, name = PackedUp.NAME, version = PackedUp.VERSION)
public class PackedUp {

    public static final String MODID = "packedup";
    public static final String NAME = "Packed Up";
    public static final String VERSION = "1.0.11";

    @Mod.Instance
    public static PackedUp instance;

    public static SimpleNetworkWrapper channel;

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
        PUConfig.init(e.getModConfigurationDirectory());
        channel = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        channel.registerMessage(PacketRename.class, PacketRename.class, 0, Side.SERVER);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e){
        MinecraftForge.EVENT_BUS.register(BackpackStorageManager.class);
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    }

}
