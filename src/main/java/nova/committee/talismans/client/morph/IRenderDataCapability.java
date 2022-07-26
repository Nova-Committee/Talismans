package nova.committee.talismans.client.morph;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import nova.committee.talismans.client.morph.animation.AbstractMorphChangeAnimation;
import nova.committee.talismans.client.morph.handler.IEntitySynchronizer;
import nova.committee.talismans.client.morph.handler.IEntitySynchronizerWithRotation;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/26 12:18
 * Version: 1.0
 */
public interface IRenderDataCapability
{
    /**
     * Use this method to set the animation for the morph entity.
     *
     * @param animation This is an optional that represents the animation. The
     *                  optional may be empty to indicate that no animation is
     *                  present currently.
     **/
    void setAnimation(Optional<AbstractMorphChangeAnimation> animation);

    /** Ticks the animation, if it is present. This may lead to a change of the internal state of this capability. **/
    void tickAnimation();

    /** Renders the animation, if it is present. **/
    void renderAnimation(Player player, PoseStack poseStack, float partialTicks, MultiBufferSource buffer, int light);

    /**
     * This method returns whether an animation is present or not.
     *
     *  @return A boolean indicating whether an animation is present or not.
     **/
    boolean hasAnimation();

    /**
     * This method gets or creates a cached entity for the player.
     *
     * @param player The player to whom this capability belongs.
     **/
    Entity getOrCreateCachedEntity(Player player);

    /**
     * Gets or creates a cached synchronizer.
     *
     * @param player The player to whom this capability belongs.
     **/
    ArrayList<IEntitySynchronizer> getOrCreateCachedSynchronizers(Player player);

    /**
     * Gets or creates a cached list of rotation synchronizers.
     *
     * @param player The player to whom this capability belongs.
     */
    ArrayList<IEntitySynchronizerWithRotation> getOrCreateCachedRotationSynchronizers(Player player);

    /**
     * Sets the cached entity and invalidates the cache.
     *
     * @param entity The entity that should be set as the new entity.
     **/
    void setEntity(Entity entity);

    /** Invalidates the cache. **/
    void invalidateCache();
}
