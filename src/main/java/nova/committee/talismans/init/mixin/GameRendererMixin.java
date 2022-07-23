package nova.committee.talismans.init.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import nova.committee.talismans.MixinStatic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/23 23:40
 * Version: 1.0
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {


    @Inject(at = @At("HEAD"), method = "pick(F)V")
    private void onBeforePick(float vec33, CallbackInfo info) {
        MixinStatic.insidePick = true;
    }

    @Inject(at = @At("TAIL"), method = "pick(F)V")
    private void onAfterPick(float vec33, CallbackInfo info) {
        MixinStatic.insidePick = false;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/CameraType;isFirstPerson()Z"), method = "renderItemInHand(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/Camera;F)V", require = 0)
    private void onRenderItemInHand(PoseStack p_109121_, Camera p_109122_, float p_109123_, CallbackInfo info) {
        MixinStatic.insideRenderItemInHand = true;
    }
}
