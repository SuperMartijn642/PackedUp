package com.supermartijn642.packedup.mixin;

import com.supermartijn642.packedup.PackedUpCommon;
import com.supermartijn642.packedup.extensions.PackedUpPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private void addAdditionalSaveData(ValueOutput output, CallbackInfo ci){
        if(this.backpacks != null && !this.backpacks.isEmpty()){
            ValueOutput.TypedOutputList<ItemStack> itemData = output.list("packedup:backpacks", ItemStack.CODEC);
            this.backpacks.forEach(itemData::add);
        }
    }

    @Inject(
        method = "readAdditionalSaveData",
        at = @At("TAIL")
    )
    private void readAdditionalSaveData(ValueInput input, CallbackInfo ci){
        Optional<ValueInput.TypedInputList<ItemStack>> itemData = input.list("packedup:backpacks", ItemStack.CODEC);
        if(itemData.isPresent()){
            this.backpacks = itemData.get().stream().toList();
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
