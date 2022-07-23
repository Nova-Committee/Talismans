package nova.committee.talismans.init.mixin;

import net.minecraft.client.MouseHandler;
import nova.committee.talismans.MixinStatic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/23 23:54
 * Version: 1.0
 */

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
    @Inject(at = @At("HEAD"), method = "turnPlayer()V")
    private void onBeforeTurnPlayer(CallbackInfo info) {
        MixinStatic.insideTurnPlayer = true;
    }

    @Inject(at = @At("TAIL"), method = "turnPlayer()V")
    private void onAfterTurnPlayer(CallbackInfo info) {
        MixinStatic.insideTurnPlayer = false;
    }
}
