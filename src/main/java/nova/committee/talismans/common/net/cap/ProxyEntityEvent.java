package nova.committee.talismans.common.net.cap;

import cn.evolvefield.mods.atomlib.common.net.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent.Context;
import nova.committee.talismans.common.morph.cap.RenderDataCapabilityProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;
import java.util.function.Supplier;

public class ProxyEntityEvent extends IPacket<ProxyEntityEvent.ProxyEntityEventPacket>
{
	private Logger LOGGER = LogManager.getLogger();

	@Override
	public void write(ProxyEntityEventPacket packet, FriendlyByteBuf buffer)
	{
		buffer.writeUUID(packet.getPlayer());
		buffer.writeByte(packet.getEntityEventId());
	}

	@Override
	public ProxyEntityEventPacket read(FriendlyByteBuf buffer)
	{
		return new ProxyEntityEventPacket(buffer.readUUID(), buffer.readByte());
	}

	@Override
	public void run(ProxyEntityEventPacket packet, Supplier<Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			// Potentially dangerous
			Player foundPlayer = Minecraft.getInstance().level.getPlayerByUUID(packet.getPlayer());

			if(foundPlayer == null)
			{
				LOGGER.warn("Could not find player with the UUID {}", packet.getPlayer());
			}
			else
			{
				foundPlayer.getCapability(RenderDataCapabilityProvider.RENDER_CAP).ifPresent(cap ->
				{
					Entity cachedEntity = cap.getOrCreateCachedEntity(foundPlayer);

					if(cachedEntity != null)
					{
						cachedEntity.handleEntityEvent(packet.getEntityEventId());
					}
				});
			}

			ctx.get().setPacketHandled(true);
		});
	}

	public static class ProxyEntityEventPacket
	{
		private UUID player;
		private byte entityEventId;

		public ProxyEntityEventPacket(Player player, byte entityEventId)
		{
			this(player.getUUID(), entityEventId);
		}

		public ProxyEntityEventPacket(UUID player, byte entityEventId)
		{
			this.player = player;
			this.entityEventId = entityEventId;
		}

		public UUID getPlayer()
		{
			return player;
		}

		public byte getEntityEventId()
		{
			return entityEventId;
		}
	}
}
