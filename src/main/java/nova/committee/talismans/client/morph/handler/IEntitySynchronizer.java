package nova.committee.talismans.client.morph.handler;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/26 12:19
 * Version: 1.0
 */
public interface IEntitySynchronizer
{
    /** This method should return true if attributes from the given entity shall be set. **/
    boolean appliesToMorph(Entity morphEntity);

    /**
     * This method will be called when
     * {@link IEntitySynchronizer#appliesToMorph(Entity)} returns {@code true}.
     * <br>
     * <br>
     * It should read attributes from the player and set them properly in the
     * entity, e. g. if you are morphed into a skeleton, and the player holds a bow,
     * this very bow should then be transferred into the skeletons inventory.
     **/
    void applyToMorphEntity(Entity morphEntity, Player player);

    /**
     * This method will be called when {@link IEntitySynchronizer#appliesToMorph(Entity)} returns {@code true}. It will be called after the morph entity has ticked.
     * Use this to update location and rotation.
     *
     * @param morphEntity The morph entity that is being rendered on the screen.
     * @param player The hidden player from which the attributes shall be copied.
     */
    default void applyToMorphEntityPostTick(Entity morphEntity, Player player) {};
}
