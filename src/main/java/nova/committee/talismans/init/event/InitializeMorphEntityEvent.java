package nova.committee.talismans.init.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/26 12:23
 * Version: 1.0
 */
public class InitializeMorphEntityEvent extends Event
{
    private Player player;
    private Entity morphEntity;

    public InitializeMorphEntityEvent(Player player, Entity morphEntity)
    {
        this.player = player;
        this.morphEntity = morphEntity;
    }

    public Player getPlayer()
    {
        return player;
    }

    public Entity getMorphEntity()
    {
        return morphEntity;
    }
}
