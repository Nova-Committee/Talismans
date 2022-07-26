package nova.committee.talismans.client.morph.skin;

import net.minecraft.world.entity.player.Player;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/26 12:28
 * Version: 1.0
 */
public class DummyShrinkAPIInteract implements IShrinkAPIInteract
{
    @Override
    public float getShrinkingValue(Player player)
    {
        return 1;
    }

    @Override
    public boolean isShrunk(Player player)
    {
        return false;
    }
}
