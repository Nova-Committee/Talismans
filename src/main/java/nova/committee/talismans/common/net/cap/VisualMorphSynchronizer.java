package nova.committee.talismans.common.net.cap;

import cn.evolvefield.mods.atomlib.common.net.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import nova.committee.talismans.Static;
import nova.committee.talismans.common.morph.VisualMorphDataRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

public class VisualMorphSynchronizer extends IPacket<VisualMorphSynchronizer.VisualMorphPacket>
{
	@Override
	public void write(VisualMorphPacket packet, FriendlyByteBuf buffer)
	{
		buffer.writeCollection(packet.getData(), (bufferToWriteTo, visualMorphData) ->
		{
			bufferToWriteTo.writeFloat(visualMorphData.getScale());
			bufferToWriteTo.writeResourceLocation(visualMorphData.getRegistryName());
		});
	}

	@Override
	public VisualMorphPacket read(FriendlyByteBuf buffer)
	{
		return new VisualMorphPacket(buffer.readCollection(length -> new ArrayList<>(length), bufferToReadFrom ->
		{
			VisualMorphDataRegistry.VisualMorphData data = new VisualMorphDataRegistry.VisualMorphData(bufferToReadFrom.readFloat());
			data.setRegistryName(bufferToReadFrom.readResourceLocation());
			return data;
		}));
	}

	@Override
	public void run(VisualMorphPacket packet, Supplier<Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			Static.VISUAL_MORPH_DATA.clear();

			for(VisualMorphDataRegistry.VisualMorphData data : packet.getData())
				Static.VISUAL_MORPH_DATA.addVisualMorphData(data);

			ctx.get().setPacketHandled(true);
		});
	}

	public static class VisualMorphPacket
	{
		private Collection<VisualMorphDataRegistry.VisualMorphData> data;

		public VisualMorphPacket(Collection<VisualMorphDataRegistry.VisualMorphData> data)
		{
			this.data = data;
		}

		public Collection<VisualMorphDataRegistry.VisualMorphData> getData()
		{
			return data;
		}
	}
}
