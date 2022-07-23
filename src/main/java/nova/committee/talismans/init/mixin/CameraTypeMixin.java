package nova.committee.talismans.init.mixin;

import net.minecraft.client.CameraType;
import nova.committee.talismans.MixinStatic;
import nova.committee.talismans.client.camera.FreeCamera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/23 23:56
 * Version: 1.0
 */

@Mixin(CameraType.class)
public class CameraTypeMixin {
    @Inject(at = @At("HEAD"), method = "isFirstPerson()Z", cancellable = true)
    private void onIsFirstPerson(CallbackInfoReturnable<Boolean> info) {
        if (!FreeCamera.instance.isActive()) {
            MixinStatic.insideRenderItemInHand = false;
            return;
        }
        if (MixinStatic.insideRenderCrosshair) {
            info.setReturnValue(true);
            info.cancel();
            return;
        }
        if (MixinStatic.insideRenderItemInHand) {
            MixinStatic.insideRenderItemInHand = false;
            if (FreeCamera.instance.shouldRenderHands()) {
                info.setReturnValue(true);
            }
        }
    }
}
