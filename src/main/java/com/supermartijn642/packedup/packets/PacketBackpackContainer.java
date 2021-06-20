package com.supermartijn642.packedup.packets;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.network.BasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.packedup.BackpackContainer;
import com.supermartijn642.packedup.BackpackInventory;
import com.supermartijn642.packedup.BackpackStorageManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

import java.util.HashSet;
import java.util.Set;

/**
 * Created 6/29/2020 by SuperMartijn642
 */
public class PacketBackpackContainer implements BasePacket {

    private int inventoryIndex;
    private Set<Integer> bagsInThisBag;
    private Set<Integer> bagsThisBagIsIn;
    private int layer;

    public PacketBackpackContainer(int inventoryIndex){
        this.inventoryIndex = inventoryIndex;
    }

    public PacketBackpackContainer(){
    }

    @Override
    public void write(PacketBuffer buffer){
        BackpackInventory inventory = BackpackStorageManager.getInventory(this.inventoryIndex);
        buffer.writeInt(inventory.bagsInThisBag.size());
        inventory.bagsInThisBag.forEach(buffer::writeInt);
        buffer.writeInt(inventory.bagsThisBagIsIn.size());
        inventory.bagsThisBagIsIn.forEach(buffer::writeInt);
        buffer.writeInt(inventory.layer);
    }

    @Override
    public void read(PacketBuffer buffer){
        int size = buffer.readInt();
        this.bagsInThisBag = new HashSet<>(size);
        for(int i = 0; i < size; i++)
            this.bagsInThisBag.add(buffer.readInt());
        size = buffer.readInt();
        this.bagsThisBagIsIn = new HashSet<>(size);
        for(int i = 0; i < size; i++)
            this.bagsThisBagIsIn.add(buffer.readInt());
        this.layer = buffer.readInt();
    }

    @Override
    public void handle(PacketContext context){
        EntityPlayer player = ClientUtils.getPlayer();
        if(player.openContainer instanceof BackpackContainer){
            BackpackContainer container = (BackpackContainer)player.openContainer;
            container.inventory.bagsInThisBag.addAll(this.bagsInThisBag);
            container.inventory.bagsThisBagIsIn.addAll(this.bagsThisBagIsIn);
            container.inventory.layer = this.layer;
        }
    }
}
