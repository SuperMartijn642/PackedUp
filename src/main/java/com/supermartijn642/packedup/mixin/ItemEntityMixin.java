package com.supermartijn642.packedup.mixin;

import com.supermartijn642.packedup.BackpackItem;
import com.supermartijn642.packedup.PackedUpConfig;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Created 17/07/2024 by SuperMartijn642
 */
@Mixin(ItemEntity.class)
public class ItemEntityMixin {

    @Inject(
        method = "fireImmune()Z",
        at = @At("RETURN"),
        cancellable = true
    )
    private void fireImmune(CallbackInfoReturnable<Boolean> ci) {
        //noinspection ConstantValue,DataFlowIssue
        if(!ci.getReturnValue()
            && !PackedUpConfig.canBackpacksBurn.get()
            && ((ItemEntity)(Object)this).getItem().getItem() instanceof BackpackItem)
            ci.setReturnValue(true);
    }
}
