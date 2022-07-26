package nova.committee.talismans.init.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import nova.committee.talismans.client.morph.handler.IEntitySynchronizer;
import nova.committee.talismans.client.morph.IRenderDataCapability;
import nova.committee.talismans.client.morph.skin.ShrinkAPIInteractor;
import nova.committee.talismans.common.morph.MorphItem;
import nova.committee.talismans.common.morph.cap.IMorphCapability;
import nova.committee.talismans.common.morph.cap.MorphCapabilityAttacher;
import nova.committee.talismans.common.morph.cap.RenderDataCapabilityProvider;
import nova.committee.talismans.common.morph.player.AdvancedAbstractClientPlayerEntity;
import nova.committee.talismans.init.event.InitializeMorphEntityEvent;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/26 12:25
 * Version: 1.0
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class RenderHandler
{
    private static boolean veryDodgyStackOverflowPreventionHackJesJes = false;

    @SubscribeEvent
    public static void onMorphInit(InitializeMorphEntityEvent event)
    {
        if(event.getPlayer() == Minecraft.getInstance().player)
            event.getMorphEntity().setCustomNameVisible(false);

        if(event.getMorphEntity() instanceof AbstractClientPlayer entity)
        {
            entity.setMainArm(event.getPlayer().getMainArm() == HumanoidArm.LEFT ? HumanoidArm.RIGHT : HumanoidArm.LEFT);
        }

        if(event.getMorphEntity() instanceof AdvancedAbstractClientPlayerEntity advanced)
        {
            advanced.setIsWearing(part -> event.getPlayer().isModelPartShown(part));
        }

        if(event.getMorphEntity() instanceof Mob mob)
        {
            mob.setLeftHanded(event.getPlayer().getMainArm() == HumanoidArm.RIGHT);
        }
    }

    public static void onBuildNewEntity(Player player, IMorphCapability capability, MorphItem aboutToMorphTo)
    {
        IRenderDataCapability renderDataCapability = player.getCapability(RenderDataCapabilityProvider.RENDER_CAP).resolve().get();

        renderDataCapability.invalidateCache();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRenderedHandler(RenderPlayerEvent.Pre event)
    {
        if(veryDodgyStackOverflowPreventionHackJesJes)
            return;

        veryDodgyStackOverflowPreventionHackJesJes = true;
        IRenderDataCapability renderDataCapability = event.getPlayer().getCapability(RenderDataCapabilityProvider.RENDER_CAP).resolve().orElse(null);

        if(renderDataCapability != null)
        {
            LazyOptional<IMorphCapability> morph = event.getPlayer().getCapability(MorphCapabilityAttacher.MORPH_CAP);

            if(renderDataCapability.hasAnimation())
            {
                renderDataCapability.renderAnimation(event.getPlayer(), event.getPoseStack(), event.getPartialTick(), event.getMultiBufferSource(), event.getPackedLight());
                event.setCanceled(true);
            }
            else if(morph.isPresent())
            {
                Optional<MorphItem> currentMorph = morph.resolve().get().getCurrentMorph();

                if(currentMorph.isPresent())
                {
                    event.setCanceled(true);

                    Player player = event.getPlayer();
                    Entity toRender = renderDataCapability.getOrCreateCachedEntity(player);

                    renderDataCapability.getOrCreateCachedRotationSynchronizers(player).forEach(rotationSync -> rotationSync.updateMorphRotation(toRender, player));
                    renderMorph(player, toRender, event.getPoseStack(), event.getPartialTick(), event.getMultiBufferSource(), event.getPackedLight());
                }
            }
        }

        veryDodgyStackOverflowPreventionHackJesJes = false;
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.side == LogicalSide.CLIENT && event.phase == TickEvent.Phase.END)
        {

            IRenderDataCapability renderDataCapability = event.player.getCapability(RenderDataCapabilityProvider.RENDER_CAP).resolve().orElse(null);

            if(renderDataCapability != null)
            {
                renderDataCapability.tickAnimation();

                ArrayList<IEntitySynchronizer> syncs = renderDataCapability.getOrCreateCachedSynchronizers(event.player);

                Entity entity = renderDataCapability.getOrCreateCachedEntity(event.player);

                if(syncs != null)
                {
                    syncs.forEach(sync -> sync.applyToMorphEntity(entity, event.player));
                }

                if(entity != null)
                {
                    entity.tick();
                }

                if(syncs != null)
                {
                    syncs.forEach(sync -> sync.applyToMorphEntityPostTick(entity, event.player));
                }
            }
        }
    }

    public static void renderMorph(Player player, Entity toRender, PoseStack matrixStack, float partialRenderTicks, MultiBufferSource buffers, int light)
    {
        float divisor = ShrinkAPIInteractor.getInteractor().getShrinkingValue(player);

        matrixStack.pushPose();

        if(ShrinkAPIInteractor.getInteractor().isShrunk(player))
            matrixStack.scale(0.81f / divisor, 0.81f / divisor, 0.81f / divisor);

        if(toRender.isCrouching() && ShrinkAPIInteractor.getInteractor().isShrunk(player))
            matrixStack.translate(0, 1, 0);

        if(player.isCrouching() )
        {
            toRender.setPose(Pose.STANDING);
            matrixStack.translate(0, 0.125D, 0);
        }

        EntityRenderer<? super Entity> manager = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(toRender);
        manager.render(toRender, 0, partialRenderTicks, matrixStack, buffers, manager.getPackedLightCoords(toRender, partialRenderTicks));

        matrixStack.popPose();
    }
}
