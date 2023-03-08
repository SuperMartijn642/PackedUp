package com.supermartijn642.packedup.mixin;

import com.supermartijn642.packedup.PackedUpCommon;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created 08/03/2023 by SuperMartijn642
 */
@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @Inject(
        method = "restoreFrom",
        at = @At("TAIL")
    )
    private void restoreFrom(ServerPlayer oldPlayer, boolean bl, CallbackInfo ci){
        //noinspection DataFlowIssue
        ServerPlayer player = (ServerPlayer)(Object)this;
        PackedUpCommon.onPlayerClone(player, oldPlayer);
    }
}
