package nova.committee.talismans.init.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import nova.committee.talismans.MixinStatic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/23 23:55
 * Version: 1.0
 */

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Inject(at = @At("HEAD"), method = "renderCrosshair(Lcom/mojang/blaze3d/vertex/PoseStack;)V")
    private void onBeforeRenderCrosshair(PoseStack posestack, CallbackInfo info) {
        MixinStatic.insideRenderCrosshair = true;
    }

    @Inject(at = @At("TAIL"), method = "renderCrosshair(Lcom/mojang/blaze3d/vertex/PoseStack;)V")
    private void onAfterRenderCrosshair(PoseStack posestack, CallbackInfo info) {
        MixinStatic.insideRenderCrosshair = false;
    }
}
