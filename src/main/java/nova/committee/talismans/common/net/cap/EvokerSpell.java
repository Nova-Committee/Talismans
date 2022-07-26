package nova.committee.talismans.common.net.cap;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent.Context;
import nova.committee.talismans.common.morph.cap.instance.evoker.EvokerSpellCapabilityInstance;
import nova.committee.talismans.common.morph.cap.instance.evoker.IEvokerSpellCapability;

import java.util.function.Supplier;

public class EvokerSpell extends CommonCapabilitySynchronizer<EvokerSpell.EvokerSpellPacket, IEvokerSpellCapability>
{
	public EvokerSpell()
	{
		super(EvokerSpellCapabilityInstance.EVOKER_SPELL_CAP);
	}

	@Override
	public void encodeAdditional(EvokerSpellPacket packet, FriendlyByteBuf buffer)
	{
		buffer.writeInt(packet.getSpellTicksLeft());
	}

	@Override
	public EvokerSpellPacket decodeAdditional(FriendlyByteBuf buffer)
	{
		return new EvokerSpellPacket(buffer.readInt());
	}

	@Override
	public boolean handleCapabilitySync(EvokerSpellPacket packet, Supplier<Context> ctx, Player player, IEvokerSpellCapability capabilityInterface)
	{
		capabilityInterface.setCastingTicks(packet.getSpellTicksLeft());

		return true;
	}

	public static class EvokerSpellPacket extends CommonCapabilitySynchronizer.Packet
	{
		private int spellTicksLeft;

		public EvokerSpellPacket(int spellTicksLeft)
		{
			this.spellTicksLeft = spellTicksLeft;
		}

		public int getSpellTicksLeft()
		{
			return spellTicksLeft;
		}

		public void setSpellTicksLeft(int spellTicksLeft)
		{
			this.spellTicksLeft = spellTicksLeft;
		}
	}
}
