package com.supermartijn642.packedup;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
@Mod.EventBusSubscriber
public class BackpackStorageManager {

    private static File directory;
    private static long lastSaveTimestamp = 0;
    private static final HashMap<Integer,BackpackInventory> inventories = new HashMap<>();
    private static int inventoryIndex = 0;

    public static final Supplier<Integer> maxLayers = () -> PackedUpConfig.allowBagInBag.get() ? PackedUpConfig.maxBagInBagLayer.get() : 0;

    @SubscribeEvent
    public static void onLevelSave(LevelEvent.Save event){
        if(event.getLevel().isClientSide() || !(event.getLevel() instanceof Level) || (((Level)event.getLevel()).dimension() != Level.OVERWORLD && System.currentTimeMillis() - lastSaveTimestamp < 30000))
            return;
        save();
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event){
        if(event.getLevel().isClientSide() || !(event.getLevel() instanceof Level) || ((Level)event.getLevel()).dimension() != Level.OVERWORLD)
            return;
        ServerLevel world = (ServerLevel)event.getLevel();
        directory = new File(world.getServer().getWorldPath(LevelResource.ROOT).toFile(), "packedup/backpacks");
        load();
    }

    public static BackpackInventory getInventory(int index){
        BackpackInventory inventory = inventories.get(index);
        if(inventory == null){
            File file = new File(directory, "inventory" + index + ".nbt");
            if(file.exists()){
                inventory = new BackpackInventory(false, index);
                inventory.load(file);
                inventories.put(index, inventory);
            }
        }
        return inventory;
    }

    public static int createInventoryIndex(BackpackType type){
        int index = inventoryIndex++;
        inventories.put(index, new BackpackInventory(false, index, type.getSlots()));
        return index;
    }

    public static void save(){
        directory.mkdirs();
        for(int i : inventories.keySet())
            inventories.get(i).save(new File(directory, "inventory" + i + ".nbt"));
        lastSaveTimestamp = System.currentTimeMillis();
    }

    public static void load(){
        File[] files = directory.listFiles();
        inventories.clear();
        if(files == null)
            files = new File[0];
        int highest = -1;
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

            // for validation
            BackpackInventory inventory = new BackpackInventory(false, index);
            inventory.load(file);
            inventory.bagsThisBagIsIn.clear();
            inventory.bagsThisBagIsDirectlyIn.clear();
            inventory.bagsInThisBag.clear();
            inventory.bagsDirectlyInThisBag.clear();
            inventory.layer = 0;
            inventories.put(index, inventory);
        }

        inventoryIndex = highest + 1;

        // validation
        for(Map.Entry<Integer,BackpackInventory> entry : inventories.entrySet()){
            BackpackInventory inventory = entry.getValue();
            for(ItemStack stack : inventory.getStacks()){
                if(stack.getItem() instanceof BackpackItem && stack.getOrCreateTag().contains("packedup:invIndex")){
                    int index = stack.getTag().getInt("packedup:invIndex");
                    if(!inventories.containsKey(index)){
                        stack.getTag().remove("packedup:invIndex");
                        continue;
                    }
                    inventory.bagsDirectlyInThisBag.add(index);
                    inventories.get(index).bagsThisBagIsDirectlyIn.add(entry.getKey());
                }
            }
        }

        for(Map.Entry<Integer,BackpackInventory> entry : inventories.entrySet()){
            BackpackInventory inventory = entry.getValue();
            inventory.layer = getBagsThisBagIsIn(entry.getKey(), inventory.bagsThisBagIsIn);
            getBagsInThisBag(entry.getKey(), inventory.bagsInThisBag);
        }

        save();
        inventories.clear();
    }

    private static Integer getBagsThisBagIsIn(int index, Set<Integer> bags){
        if(getInventory(index) == null)
            return 0;

        int highest = 0;

        for(int id : getInventory(index).bagsThisBagIsDirectlyIn){
            if(!bags.contains(id)){
                bags.add(id);
                highest = Math.max(highest, getBagsThisBagIsIn(id, bags)) + 1;
            }
        }

        return highest;
    }

    private static void getBagsInThisBag(int index, Set<Integer> bags){
        if(getInventory(index) == null)
            return;

        for(int id : getInventory(index).bagsDirectlyInThisBag){
            if(!bags.contains(id)){
                bags.add(id);
                getBagsInThisBag(id, bags);
            }
        }
    }

    public static void onInsert(int index, int to){
        getInventory(index).bagsThisBagIsDirectlyIn.add(to);
        getInventory(to).bagsDirectlyInThisBag.add(index);
        updateRelativeBags(index, to);
    }

    public static void onExtract(int index, int from){
        getInventory(index).bagsThisBagIsDirectlyIn.remove(from);
        getInventory(from).bagsDirectlyInThisBag.remove(index);
        updateRelativeBags(index, from);
    }

    private static void updateRelativeBags(int child, int parent){
        BackpackInventory childInventory = getInventory(child);
        BackpackInventory parentInventory = getInventory(parent);
        Set<Integer> bagsBefore = parentInventory.bagsThisBagIsIn;
        bagsBefore.add(parent);
        Set<Integer> bagsAfter = childInventory.bagsInThisBag;
        bagsAfter.add(child);

        for(int id : bagsBefore){
            BackpackInventory inv = getInventory(id);
            inv.bagsInThisBag.clear();
            getBagsInThisBag(id, inv.bagsInThisBag);
        }

        for(int id : bagsAfter){
            BackpackInventory inv = getInventory(id);
            inv.bagsThisBagIsIn.clear();
            inv.layer = getBagsThisBagIsIn(id, inv.bagsThisBagIsIn);
        }
    }
}
