package nova.committee.talismans.common.net.cap;

import cn.evolvefield.mods.atomlib.common.net.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

// I need this packet because the vanilla one is bs
public class EntityMovementChanged extends IPacket<EntityMovementChanged.EntityMovementChangedPacket>
{
	@Override
	public void write(EntityMovementChangedPacket packet, FriendlyByteBuf buffer)
	{
		buffer.writeInt(packet.getEntityId());
		buffer.writeDouble(packet.getDeltaMovement().x);
		buffer.writeDouble(packet.getDeltaMovement().y);
		buffer.writeDouble(packet.getDeltaMovement().z);
		buffer.writeBoolean(packet.shouldAdd());
	}

	@Override
	public EntityMovementChangedPacket read(FriendlyByteBuf buffer)
	{
		return new EntityMovementChangedPacket(buffer.readInt(), new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()), buffer.readBoolean());
	}

	@Override
	public void run(EntityMovementChangedPacket packet, Supplier<Context> ctx)
	{
		ctx.get().enqueueWork(() ->
		{
			Entity entity = Minecraft.getInstance().level.getEntity(packet.getEntityId());

			if(entity != null)
			{
				if(packet.shouldAdd())
				{
					entity.setDeltaMovement(entity.getDeltaMovement().add(packet.getDeltaMovement()));
				}
				else
				{
					entity.setDeltaMovement(packet.getDeltaMovement());
				}
			}

			ctx.get().setPacketHandled(true);
		});
	}

	public static class EntityMovementChangedPacket
	{
		private int entityId;
		private Vec3 deltaMovement;
		private boolean shouldAdd;

		public EntityMovementChangedPacket(int entityId, Vec3 deltaMovement, boolean shouldAdd)
		{
			this.entityId = entityId;
			this.deltaMovement = deltaMovement;
			this.shouldAdd = shouldAdd;
		}

		public int getEntityId()
		{
			return entityId;
		}

		public Vec3 getDeltaMovement()
		{
			return deltaMovement;
		}

		public boolean shouldAdd()
		{
			return shouldAdd;
		}
	}
}
