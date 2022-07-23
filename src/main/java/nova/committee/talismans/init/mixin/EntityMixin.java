package nova.committee.talismans.init.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import nova.committee.talismans.MixinStatic;
import nova.committee.talismans.client.camera.FreeCamera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/23 23:42
 * Version: 1.0
 */

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow(aliases = "Lnet/minecraft/world/entity/Entity;calculateViewVector(FF)Lnet/minecraft/world/phys/Vec3;")
    protected abstract Vec3 calculateViewVector(float p_20172_, float p_20173_);

    @Inject(at = @At("HEAD"), method = "turn(DD)V", cancellable = true)
    private void onTurn(double yRot, double xRot, CallbackInfo info) {
        if (!MixinStatic.insideTurnPlayer) {
            return;
        }
        if (!FreeCamera.instance.isActive()) {
            return;
        }
        var entity = (Entity) (Object) this;
        if (entity instanceof LocalPlayer) {
            FreeCamera.instance.onMouseTurn(yRot, xRot);
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "getEyePosition(F)Lnet/minecraft/world/phys/Vec3;", cancellable = true)
    private void onGetEyePosition(float p_20300_, CallbackInfoReturnable<Vec3> info) {
        FreeCamera freeCam = FreeCamera.instance;
        if (freeCam.isActive() && freeCam.shouldOverridePlayerPosition()) {
            var entity = (Entity) (Object) this;
            if (entity instanceof LocalPlayer) {
                info.setReturnValue(new Vec3(freeCam.getX(), freeCam.getY(), freeCam.getZ()));
                info.cancel();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "getViewVector(F)Lnet/minecraft/world/phys/Vec3;", cancellable = true)
    private void onGetViewVector(float p_20253_, CallbackInfoReturnable<Vec3> info) {
        FreeCamera freeCam = FreeCamera.instance;
        if (freeCam.isActive() && freeCam.shouldOverridePlayerPosition()) {
            var entity = (Entity) (Object) this;
            if (entity instanceof LocalPlayer) {
                info.setReturnValue(this.calculateViewVector(freeCam.getXRot(), freeCam.getYRot()));
                info.cancel();
            }
        }
    }
}
