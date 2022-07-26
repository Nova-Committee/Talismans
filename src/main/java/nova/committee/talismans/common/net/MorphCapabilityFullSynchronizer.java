package nova.committee.talismans.common.net;

import cn.evolvefield.mods.atomlib.common.net.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkEvent;
import nova.committee.talismans.common.morph.MorphItem;
import nova.committee.talismans.common.morph.MorphList;
import nova.committee.talismans.common.morph.cap.IMorphCapability;
import nova.committee.talismans.common.morph.cap.MorphCapabilityAttacher;
import nova.committee.talismans.init.handler.MorphHandler;
import nova.committee.talismans.util.MorphUtil;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class MorphCapabilityFullSynchronizer extends IPacket<MorphCapabilityFullSynchronizer>
{
	private Optional<MorphItem> entityData;
	private Optional<Integer> entityIndex;
	private MorphList morphList;
	private UUID player;

	public MorphCapabilityFullSynchronizer(){}

	public MorphCapabilityFullSynchronizer(Optional<MorphItem> entityData, Optional<Integer> entityIndex, MorphList morphList,  UUID player)
	{
		this.entityData = entityData;
		this.player = player;
		this.morphList = morphList;
		this.entityIndex = entityIndex;
	}


	public Optional<MorphItem> getEntityData()
	{
		return entityData;
	}

	public UUID getPlayer()
	{
		return player;
	}

	public MorphList getMorphList()
	{
		return morphList;
	}


	public Optional<Integer> getEntityIndex()
	{
		return entityIndex;
	}

	@Override
	public void write(MorphCapabilityFullSynchronizer packet, FriendlyByteBuf buffer)
	{
		buffer.writeUUID(packet.player);
		packet.morphList.serializePacket(buffer);
		buffer.writeBoolean(packet.entityData.isPresent());
		buffer.writeBoolean(packet.entityIndex.isPresent());
		packet.getEntityData().ifPresent(data -> buffer.writeNbt(data.serialize()));
		packet.getEntityIndex().ifPresent(buffer::writeInt);

	}

	@Override
	public MorphCapabilityFullSynchronizer read(FriendlyByteBuf buffer)
	{
		UUID playerUUID = buffer.readUUID();

		MorphList morphList = new MorphList();
		morphList.deserializePacket(buffer);

		Optional<MorphItem> toMorph = Optional.empty();
		Optional<Integer> entityIndex = Optional.empty();

		boolean hasMorph = buffer.readBoolean(), hasIndex = buffer.readBoolean();

		if(hasMorph)
			toMorph = Optional.of(MorphHandler.deserializeMorphItem(buffer.readNbt()));

		if(hasIndex)
			entityIndex = Optional.of(buffer.readInt());


		return new MorphCapabilityFullSynchronizer(toMorph, entityIndex, morphList, playerUUID);
	}

	@Override
	public void run(MorphCapabilityFullSynchronizer packet, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			if(Minecraft.getInstance().level != null)
			{
				Player player = Minecraft.getInstance().level.getPlayerByUUID(packet.getPlayer());

				if(player != null)
				{
					LazyOptional<IMorphCapability> cap = player.getCapability(MorphCapabilityAttacher.MORPH_CAP);
					if(cap.isPresent())
					{
						IMorphCapability resolved = cap.resolve().get();
						resolved.setMorphList(packet.getMorphList());
					}
					MorphUtil.morphToClient(packet.getEntityData(), packet.getEntityIndex(), player);
					ctx.get().setPacketHandled(true);
				}
			}
		});
	}

}
