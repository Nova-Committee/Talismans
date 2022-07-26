package nova.committee.talismans.client.morph.handler;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/26 12:19
 * Version: 1.0
 */
public interface IEntitySynchronizerWithRotation extends IEntitySynchronizer
{
    void updateMorphRotation(Entity morphEntity, Player player);
}
