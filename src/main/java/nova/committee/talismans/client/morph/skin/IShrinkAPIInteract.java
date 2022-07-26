package nova.committee.talismans.client.morph.skin;

import net.minecraft.world.entity.player.Player;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/26 12:27
 * Version: 1.0
 */
public interface IShrinkAPIInteract
{
    float getShrinkingValue(Player player);
    boolean isShrunk(Player player);
}
