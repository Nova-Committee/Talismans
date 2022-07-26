package nova.committee.talismans.init.event;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;
import nova.committee.talismans.common.morph.MorphItem;
import nova.committee.talismans.common.morph.cap.IMorphCapability;

import javax.annotation.Nullable;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/26 12:16
 * Version: 1.0
 */
public abstract class PlayerMorphEvent extends Event
{
    Player player;
    IMorphCapability morphCapability;
    MorphItem aboutToMorphTo;

    public PlayerMorphEvent(Player player, IMorphCapability morphCapability, @Nullable MorphItem aboutToMorphTo)
    {
        this.player = player;
        this.morphCapability = morphCapability;
        this.aboutToMorphTo = aboutToMorphTo;
    }

    public Player getPlayer()
    {
        return player;
    }

    public IMorphCapability getMorphCapability()
    {
        return morphCapability;
    }

    @Nullable
    public MorphItem getAboutToMorphTo()
    {
        return aboutToMorphTo;
    }

    public static abstract class Server extends PlayerMorphEvent
    {
        public Server(Player player, IMorphCapability morphCapability, @Nullable MorphItem aboutToMorphTo)
        {
            super(player, morphCapability, aboutToMorphTo);
        }

        public static class Pre extends Server
        {

            public Pre(Player player, IMorphCapability morphCapability, MorphItem aboutToMorphTo)
            {
                super(player, morphCapability, aboutToMorphTo);
            }

            @Override
            public boolean isCancelable()
            {
                return true;
            }
        }

        public static class Post extends Server
        {

            public Post(Player player, IMorphCapability morphCapability, MorphItem aboutToMorphTo)
            {
                super(player, morphCapability, aboutToMorphTo);
            }

            @Override
            public boolean isCancelable()
            {
                return false;
            }
        }
    }

    public static abstract class Client extends PlayerMorphEvent
    {
        public Client(Player player, IMorphCapability morphCapability, @Nullable MorphItem aboutToMorphTo)
        {
            super(player, morphCapability, aboutToMorphTo);
        }

        public static class Pre extends Client
        {

            public Pre(Player player, IMorphCapability morphCapability, MorphItem aboutToMorphTo)
            {
                super(player, morphCapability, aboutToMorphTo);
            }

            @Override
            public boolean isCancelable()
            {
                return false;
            }
        }

        public static class Post extends Client
        {

            public Post(Player player, IMorphCapability morphCapability, MorphItem aboutToMorphTo)
            {
                super(player, morphCapability, aboutToMorphTo);
            }

            @Override
            public boolean isCancelable()
            {
                return false;
            }
        }
    }
}
