package nova.committee.talismans.common.net;

import cn.evolvefield.mods.atomlib.common.net.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkEvent.Context;
import nova.committee.talismans.common.morph.MorphItem;
import nova.committee.talismans.common.morph.cap.IMorphCapability;
import nova.committee.talismans.common.morph.cap.MorphCapabilityAttacher;
import nova.committee.talismans.init.handler.MorphHandler;

import java.util.UUID;
import java.util.function.Supplier;

public class MorphAddedSynchronizer extends IPacket<MorphAddedSynchronizer>
{

	UUID playerUUID;
	MorphItem addedMorph;

	public MorphAddedSynchronizer(){}

	public MorphAddedSynchronizer(UUID playerUUID, MorphItem addedMorph)
	{
		this.playerUUID = playerUUID;
		this.addedMorph = addedMorph;
	}

	public MorphItem getAddedMorph()
	{
		return addedMorph;
	}

	public UUID getPlayerUUID()
	{
		return playerUUID;
	}
	@Override
	public void write(MorphAddedSynchronizer packet, FriendlyByteBuf buffer)
	{
		buffer.writeUUID(packet.getPlayerUUID());
		buffer.writeNbt(packet.getAddedMorph().serialize());
	}

	@Override
	public MorphAddedSynchronizer read(FriendlyByteBuf buffer)
	{
		return new MorphAddedSynchronizer(buffer.readUUID(), MorphHandler.deserializeMorphItem(buffer.readNbt()));
	}

	@Override
	public void run(MorphAddedSynchronizer packet, Supplier<Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			if(Minecraft.getInstance().level != null)
			{

				LazyOptional<IMorphCapability> cap = Minecraft.getInstance().level.getPlayerByUUID(packet.getPlayerUUID()).getCapability(MorphCapabilityAttacher.MORPH_CAP);

				if(cap.isPresent())
				{
					IMorphCapability resolved = cap.resolve().get();

					resolved.addToMorphList(packet.getAddedMorph());
				}

				ctx.get().setPacketHandled(true);
			}
		});
	}

}
