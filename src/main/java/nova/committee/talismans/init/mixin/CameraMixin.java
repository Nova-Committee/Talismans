package nova.committee.talismans.init.mixin;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import nova.committee.talismans.client.camera.FreeCamera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/23 23:44
 * Version: 1.0
 */

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow(aliases = "Lnet/minecraft/client/Camera;setRotation(FF)V")
    protected abstract void setRotation(float p_90573_, float p_90574_);

    @Shadow(aliases = "Lnet/minecraft/client/Camera;setPosition(DDD)V")
    protected abstract void setPosition(double p_90585_, double p_90586_, double p_90587_);

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V", ordinal = 0), method = "setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V", cancellable = true)
    private void onSetup(BlockGetter level, Entity entity, boolean detached, boolean mirrored, float particalTicks, CallbackInfo info) {
        FreeCamera controller = FreeCamera.instance;
        if (FreeCamera.instance.isActive()) {
            setRotation(controller.getYRot(), controller.getXRot());
            setPosition(controller.getX(), controller.getY(), controller.getZ());
            info.cancel();
        }
    }
}
