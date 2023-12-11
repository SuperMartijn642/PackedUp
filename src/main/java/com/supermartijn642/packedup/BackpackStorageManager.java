package com.supermartijn642.packedup;

import com.supermartijn642.core.util.Holder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public class BackpackStorageManager {

    private static Path directory;
    private static long lastSaveTimestamp = 0;
    private static final HashMap<Integer,BackpackInventory> inventories = new HashMap<>();
    private static int inventoryIndex = 0;

    public static final Supplier<Integer> maxLayers = () -> PackedUpConfig.allowBagInBag.get() ? PackedUpConfig.maxBagInBagLayer.get() : 0;

    public static void registerEventListeners(){
        ServerWorldEvents.LOAD.register((server, level) -> onLevelLoad(level));
    }

    public static void onLevelSave(ServerLevel level){
        if(level.dimension() != Level.OVERWORLD && System.currentTimeMillis() - lastSaveTimestamp < 30000)
            return;
        save();
    }

    public static void onLevelLoad(ServerLevel level){
        if(level.isClientSide() || level.dimension() != Level.OVERWORLD)
            return;
        directory = level.getServer().getWorldPath(LevelResource.ROOT).resolve("packedup/backpacks");
        load();
    }

    public static BackpackInventory getInventory(int index){
        BackpackInventory inventory = inventories.get(index);
        if(inventory == null){
            Path file = directory.resolve("inventory" + index + ".nbt");
            if(Files.exists(file)){
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
        try{
            Files.createDirectories(directory);
        }catch(IOException e){
            e.printStackTrace();
            return;
        }
        for(int i : inventories.keySet())
            inventories.get(i).save(directory.resolve("inventory" + i + ".nbt"));
        lastSaveTimestamp = System.currentTimeMillis();
    }

    public static void load(){
        inventories.clear();
        Holder<Integer> highest = new Holder<>(-1);
        try(Stream<Path> files = Files.list(directory)){
            files.forEach(file -> {
                if(Files.isRegularFile(file))
                    return;
                String name = file.getFileName().toString();
                if(!name.startsWith("inventory") || !name.endsWith(".nbt"))
                    return;
                int index;
                try{
                    index = Integer.parseInt(name.substring("inventory".length(), name.length() - ".nbt".length()));
                }catch(NumberFormatException e){
                    return;
                }
                if(index > highest.get())
                    highest.set(index);

                // for validation
                BackpackInventory inventory = new BackpackInventory(false, index);
                inventory.load(file);
                inventory.bagsThisBagIsIn.clear();
                inventory.bagsThisBagIsDirectlyIn.clear();
                inventory.bagsInThisBag.clear();
                inventory.bagsDirectlyInThisBag.clear();
                inventory.layer = 0;
                inventories.put(index, inventory);
            });
        }catch(IOException e){
            e.printStackTrace();
        }

        inventoryIndex = highest.get() + 1;

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
