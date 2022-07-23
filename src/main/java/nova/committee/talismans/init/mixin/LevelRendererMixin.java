package nova.committee.talismans.init.mixin;

import net.minecraft.client.renderer.LevelRenderer;
import nova.committee.talismans.client.camera.FreeCamera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/23 23:52
 * Version: 1.0
 */
@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @ModifyArg(
            method = "renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lcom/mojang/math/Matrix4f;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;setupRender(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/culling/Frustum;ZZ)V"),
            index = 3)
    private boolean onCallSetupRender(boolean isSpectator) {
        if (FreeCamera.instance.isActive()) {
            return true;
        } else {
            return isSpectator;
        }
    }
}
