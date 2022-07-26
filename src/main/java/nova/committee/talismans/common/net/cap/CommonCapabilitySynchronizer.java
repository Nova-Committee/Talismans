package nova.committee.talismans.common.net.cap;

import cn.evolvefield.mods.atomlib.common.net.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/26 16:19
 * Version: 1.0
 */
public abstract class CommonCapabilitySynchronizer<P extends CommonCapabilitySynchronizer.Packet, C> extends IPacket<P> {
    public static class Packet
    {
        private UUID player;

        public UUID getPlayer()
        {
            return player;
        }

        public void setPlayer(UUID player)
        {
            this.player = player;
        }
    }

    private Logger logger = LogManager.getLogger();

    private Capability<C> capabilityToken;

    public CommonCapabilitySynchronizer(Capability<C> capabilityToken)
    {
        this.capabilityToken = capabilityToken;
    }

    @Override
    public void write(P packet, FriendlyByteBuf buffer)
    {
        buffer.writeUUID(packet.getPlayer());
        encodeAdditional(packet, buffer);
    }

    @Override
    public P read(FriendlyByteBuf buffer)
    {
        UUID player = buffer.readUUID();
        P packet = decodeAdditional(buffer);
        packet.setPlayer(player);
        return packet;
    }

    @Override
    public void run(P packet, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            if(Minecraft.getInstance().level == null)
            {
                logger.info("Can't process capability sync packet because level is null");
            }
            else
            {
                Player player = Minecraft.getInstance().level.getPlayerByUUID(packet.getPlayer());

                if(player == null)
                {
                    logger.info(MessageFormat.format("Can't process capability sync packet because the player with the UUID {0} could not be found.", packet.getPlayer().toString()));
                }
                else
                {
                    Optional<C> capabilityInterface = player.getCapability(capabilityToken).resolve();

                    if(capabilityInterface.isPresent())
                    {
                        ctx.get().setPacketHandled(handleCapabilitySync(packet, ctx, player, capabilityInterface.get()));
                    }
                    else
                    {
                        logger.info(MessageFormat.format("Can't process capability sync packet because the capability {0} was not attached to player {1}({2})", capabilityToken.getName(), packet.getPlayer(), player.getName().getString()));
                    }
                }
            }
        });
    }

    public abstract void encodeAdditional(P packet, FriendlyByteBuf buffer);

    public abstract P decodeAdditional(FriendlyByteBuf buffer);

    public abstract boolean handleCapabilitySync(P packet, Supplier<NetworkEvent.Context> ctx, Player player, C capabilityInterface);

    public Logger getLogger()
    {
        return logger;
    }
}
