package nova.committee.talismans.client.morph.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import nova.committee.talismans.client.morph.handler.IEntitySynchronizer;
import nova.committee.talismans.init.handler.RenderHandler;

import java.util.ArrayList;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/26 12:30
 * Version: 1.0
 */
public class ScaleAnimation extends AbstractMorphChangeAnimation
{

    public ScaleAnimation(Player player, Entity transitionFrom, ArrayList<IEntitySynchronizer> synchronizersFrom, Entity transitionTo,
                          ArrayList<IEntitySynchronizer> synchronizersTo, int animationDuration)
    {
        super(player, transitionFrom, synchronizersFrom, transitionTo, synchronizersTo, animationDuration);
    }

    @Override
    public void render(PoseStack matrixStack, float partialRenderTicks, MultiBufferSource buffers, int light)
    {
        float progress = getProgress(partialRenderTicks);

        // Draw old entity
        matrixStack.pushPose();
        matrixStack.scale(1 - progress, 1 - progress, 1 - progress);
        RenderHandler.renderMorph(getPlayer(), getTransitionFrom(), matrixStack, partialRenderTicks, buffers, light);
        matrixStack.popPose();

        // Draw new entity
        matrixStack.pushPose();
        matrixStack.scale(progress, progress, progress);
        RenderHandler.renderMorph(getPlayer(), getTransitionTo(), matrixStack, partialRenderTicks, buffers, light);
        matrixStack.popPose();
    }
}
