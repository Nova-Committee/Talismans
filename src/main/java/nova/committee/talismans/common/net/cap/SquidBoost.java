package nova.committee.talismans.common.net.cap;

import cn.evolvefield.mods.atomlib.common.net.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.UUID;
import java.util.function.Supplier;

public class SquidBoost extends IPacket<SquidBoost.SquidBoostPacket>
{
	@Override
	public void write(SquidBoostPacket packet, FriendlyByteBuf buffer)
	{
		buffer.writeFloat(packet.getStrength());
		buffer.writeUUID(packet.getPlayer());
	}

	@Override
	public SquidBoostPacket read(FriendlyByteBuf buffer)
	{
		return new SquidBoostPacket(buffer.readFloat(), buffer.readUUID());
	}

	@Override
	public void run(SquidBoostPacket packet, Supplier<Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			if(Minecraft.getInstance().level != null)
			{
				Player potentialPlayer = Minecraft.getInstance().level.getPlayerByUUID(packet.getPlayer());

				if(potentialPlayer != null)
				{
					potentialPlayer.setDeltaMovement(potentialPlayer.getDeltaMovement().add(potentialPlayer.getForward().multiply(packet.strength, packet.strength, packet.strength)));
					ctx.get().setPacketHandled(true);
				}
			}
		});
	}

	public static class SquidBoostPacket
	{
		float strength;
		UUID player;

		public SquidBoostPacket(float strength, UUID player)
		{
			this.strength = strength;
			this.player = player;
		}

		public float getStrength()
		{
			return strength;
		}

		public UUID getPlayer()
		{
			return player;
		}
	}
}
