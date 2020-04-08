package com.supermartijn642.packedup;

import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;
import java.util.HashMap;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
@Mod.EventBusSubscriber(Side.SERVER)
public class BackpackStorageManager {

    private static File directory;
    private static final HashMap<Integer,BackpackInventory> inventories = new HashMap<>();
    private static int inventoryIndex = 0;

    @SubscribeEvent
    public static void onWorldSave(WorldEvent.Save event){
        if(event.getWorld().isRemote || event.getWorld().provider.getDimensionType() != DimensionType.OVERWORLD)
            return;
        save();
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event){
        if(event.getWorld().isRemote || event.getWorld().provider.getDimensionType() != DimensionType.OVERWORLD)
            return;
        WorldServer world = (WorldServer)event.getWorld();
        directory = new File(world.getSaveHandler().getWorldDirectory(), "packedup/backpacks");
        load();
    }

    public static BackpackInventory getInventory(int index){
        BackpackInventory inventory = inventories.get(index);
        if(inventory == null){
            File file = new File(directory, "inventory" + index + ".nbt");
            if(!file.exists())
                throw new IllegalStateException("Inventory requested for index '" + index + "', yet no file for that index was found!");
            inventory = new BackpackInventory();
            inventory.load(file);
            inventories.put(index, inventory);
        }
        return inventory;
    }

    public static int createInventoryIndex(BackpackType type){
        int index = inventoryIndex++;
        inventories.put(index, new BackpackInventory(type.getRows() * 9));
        return index;
    }

    public static void save(){
        directory.mkdirs();
        for(int i : inventories.keySet())
            inventories.get(i).save(new File(directory, "inventory" + i + ".nbt"));
    }

    public static void load(){
        File[] files = directory.listFiles();
        if(files == null || files.length == 0)
            return;
        int highest = 0;
        for(File file : files){
            String name = file.getName();
            if(!name.startsWith("inventory") || !name.endsWith(".nbt"))
                continue;
            int index;
            try{
                index = Integer.parseInt(name.substring("inventory".length(), name.length() - ".nbt".length()));
            }catch(NumberFormatException e){continue;}
            if(index > highest)
                highest = index;
        }
        inventoryIndex = highest + 1;
    }

}
