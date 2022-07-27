package nova.committee.talismans.client.morph;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import nova.committee.talismans.client.morph.animation.AbstractMorphChangeAnimation;
import nova.committee.talismans.client.morph.handler.IEntitySynchronizer;
import nova.committee.talismans.client.morph.handler.IEntitySynchronizerWithRotation;
import nova.committee.talismans.common.morph.cap.IMorphCapability;
import nova.committee.talismans.common.morph.cap.ProxyEntityCapabilityInstance;
import nova.committee.talismans.init.event.InitializeMorphEntityEvent;
import nova.committee.talismans.init.handler.EntitySynchronizerHandler;
import nova.committee.talismans.util.MorphUtil;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/26 12:21
 * Version: 1.0
 */
public class RenderDataCapability implements IRenderDataCapability
{
    private Optional<AbstractMorphChangeAnimation> animation = Optional.empty();

    private Entity cachedEntity;
    private ArrayList<IEntitySynchronizer> cachedSynchronizers;
    private ArrayList<IEntitySynchronizerWithRotation> cachedRotationSynchronizers;

    @Override
    public void setAnimation(Optional<AbstractMorphChangeAnimation> animation)
    {
        this.animation = animation;
    }

    @Override
    public void tickAnimation()
    {
        if(animation.isPresent())
        {
            animation.get().tick();

            if(animation.get().getAnimationDuration() == animation.get().getTicks())
                animation = Optional.empty();
        }
    }

    @Override
    public void renderAnimation(Player player, PoseStack poseStack, float partialTicks, MultiBufferSource buffer, int light)
    {
        animation.ifPresent(presentAnimation -> presentAnimation.render(poseStack, partialTicks, buffer, light));
    }

    @Override
    public boolean hasAnimation()
    {
        return animation.isPresent();
    }

    @Override
    public Entity getOrCreateCachedEntity(Player player)
    {
        if(cachedEntity == null)
        {
            IMorphCapability cap = MorphUtil.getCapOrNull(player);

            if(cap != null && cap.getCurrentMorph().isPresent())
            {
                cachedEntity = cap.getCurrentMorph().get().createEntity(player.level);
                cachedEntity.getCapability(ProxyEntityCapabilityInstance.PROXY_ENTITY_CAP).ifPresent(proxyEntityCap -> proxyEntityCap.setProxyEntity(true));
                MinecraftForge.EVENT_BUS.post(new InitializeMorphEntityEvent(player, cachedEntity));
            }
        }

        return cachedEntity;
    }

    @Override
    public ArrayList<IEntitySynchronizer> getOrCreateCachedSynchronizers(Player player)
    {
        if(cachedSynchronizers == null)
        {
            Entity entity = getOrCreateCachedEntity(player);

            if(entity != null)
            {
                cachedSynchronizers = EntitySynchronizerHandler.getSynchronizersForEntity(entity);
            }
        }

        return cachedSynchronizers;
    }

    @Override
    public ArrayList<IEntitySynchronizerWithRotation> getOrCreateCachedRotationSynchronizers(Player player)
    {
        if(cachedRotationSynchronizers == null)
        {
            getOrCreateCachedSynchronizers(player);

            this.cachedRotationSynchronizers = new ArrayList<>();

            for(IEntitySynchronizer sync : this.cachedSynchronizers)
            {
                if(sync instanceof IEntitySynchronizerWithRotation withRotation)
                    this.cachedRotationSynchronizers.add(withRotation);
            }
        }

        return cachedRotationSynchronizers;
    }

    @Override
    public void setEntity(Entity entity)
    {
        invalidateCache();

        this.cachedEntity = entity;
    }

    @Override
    public void invalidateCache()
    {
        if(this.cachedEntity != null)
            this.cachedEntity.remove(Entity.RemovalReason.DISCARDED);

        this.cachedEntity = null;
        this.cachedSynchronizers = null;
        this.cachedRotationSynchronizers = null;
    }
}
