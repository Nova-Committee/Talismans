package nova.committee.talismans.common.net;

import cn.evolvefield.mods.atomlib.common.net.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import nova.committee.talismans.common.morph.MorphItem;
import nova.committee.talismans.init.handler.MorphHandler;
import nova.committee.talismans.util.MorphUtil;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class MorphChangedSynchronizer extends IPacket<MorphChangedSynchronizer>
{

	UUID playerUUID;
	Optional<Integer> morphIndex;
	Optional<MorphItem> morphItem;
	public MorphChangedSynchronizer(){}

	public MorphChangedSynchronizer(UUID playerUUID, Optional<Integer> morphIndex, Optional<MorphItem> morphItem)
	{
		this.playerUUID = playerUUID;
		this.morphIndex = morphIndex;
		this.morphItem = morphItem;
	}

	public Optional<Integer> getMorphIndex()
	{
		return morphIndex;
	}

	public Optional<MorphItem> getMorphItem()
	{
		return morphItem;
	}


	public UUID getPlayerUUID()
	{
		return playerUUID;
	}
	@Override
	public void write(MorphChangedSynchronizer packet, FriendlyByteBuf buffer)
	{
		buffer.writeUUID(packet.getPlayerUUID());

		buffer.writeBoolean(packet.getMorphIndex().isPresent());
		buffer.writeBoolean(packet.getMorphItem().isPresent());

		packet.getMorphIndex().ifPresent(index -> buffer.writeInt(index));
		packet.getMorphItem().ifPresent(item -> buffer.writeNbt(item.serialize()));

	}

	@Override
	public MorphChangedSynchronizer read(FriendlyByteBuf buffer)
	{
		UUID playerUUID = buffer.readUUID();
		boolean hasIndex = buffer.readBoolean(), hasItem = buffer.readBoolean();

		Optional<Integer> morphIndex = Optional.empty();
		Optional<MorphItem> morphItem = Optional.empty();

		if(hasIndex)
			morphIndex = Optional.of(buffer.readInt());

		if(hasItem)
			morphItem = Optional.of(MorphHandler.deserializeMorphItem(Objects.requireNonNull(buffer.readNbt())));

		return new MorphChangedSynchronizer(playerUUID, morphIndex, morphItem);
	}

	@Override
	public void run(MorphChangedSynchronizer packet, Supplier<Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			if(Minecraft.getInstance().level != null)
			{
				MorphUtil.morphToClient(packet.getMorphItem(), packet.getMorphIndex(), Minecraft.getInstance().level.getPlayerByUUID(packet.getPlayerUUID()));
				ctx.get().setPacketHandled(true);
			}
		});
	}

}
