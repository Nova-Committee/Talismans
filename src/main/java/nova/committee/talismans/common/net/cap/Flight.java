package nova.committee.talismans.common.net.cap;

import cn.evolvefield.mods.atomlib.common.net.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class Flight extends IPacket<Flight.FlightPacket>
{
	public static class FlightPacket
	{
		private boolean shouldFly;

		public FlightPacket(boolean shouldFly)
		{
			this.shouldFly = shouldFly;
		}

		public boolean shouldFly()
		{
			return shouldFly;
		}
	}

	@Override
	public void write(FlightPacket packet, FriendlyByteBuf buffer)
	{
		buffer.writeBoolean(packet.shouldFly());
	}

	@Override
	public FlightPacket read(FriendlyByteBuf buffer)
	{
		return new FlightPacket(buffer.readBoolean());
	}

	@Override
	public void run(FlightPacket packet, Supplier<Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			Minecraft.getInstance().player.getAbilities().flying = packet.shouldFly();

			ctx.get().setPacketHandled(true);
		});
	}
}
