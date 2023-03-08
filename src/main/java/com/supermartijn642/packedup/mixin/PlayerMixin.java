package com.supermartijn642.packedup.mixin;

import com.supermartijn642.packedup.PackedUpCommon;
import com.supermartijn642.packedup.extensions.PackedUpPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created 08/03/2023 by SuperMartijn642
 */
@Mixin(Player.class)
public class PlayerMixin implements PackedUpPlayer {

    private List<ItemStack> backpacks = new ArrayList<>();

    @Inject(
        method = "dropEquipment",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Inventory;dropAll()V",
            shift = At.Shift.BEFORE
        )
    )
    private void dropEquipment(CallbackInfo ci){
        //noinspection DataFlowIssue
        Player player = (Player)(Object)this;
        PackedUpCommon.onPlayerDeath(player);
    }

    @Inject(
        method = "addAdditionalSaveData",
        at = @At("TAIL")
    )
    private void addAdditionalSaveData(CompoundTag data, CallbackInfo ci){
        if(this.backpacks != null && !this.backpacks.isEmpty()){
            ListTag itemData = new ListTag();
            this.backpacks.forEach(stack -> itemData.add(stack.save(new CompoundTag())));
            data.put("packedup:backpacks", itemData);
        }
    }

    @Inject(
        method = "readAdditionalSaveData",
        at = @At("TAIL")
    )
    private void readAdditionalSaveData(CompoundTag data, CallbackInfo ci){
        if(data.contains("packedup:backpacks", Tag.TAG_LIST)){
            ListTag itemData = data.getList("packedup:backpacks", Tag.TAG_COMPOUND);
            this.backpacks = itemData.stream()
                .filter(CompoundTag.class::isInstance)
                .map(CompoundTag.class::cast)
                .map(ItemStack::of)
                .collect(Collectors.toList());
            if(this.backpacks.isEmpty())
                this.backpacks = null;
        }
    }

    @Override
    public void packedupSetBackpacks(List<ItemStack> backpacks){
        this.backpacks = backpacks == null || backpacks.isEmpty() ? null : backpacks;
    }

    @Override
    public List<ItemStack> packedupGetBackpacks(){
        return this.backpacks;
    }
}
