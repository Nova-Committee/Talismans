package nova.committee.talismans.common.morph.cap.instance.pufferfish;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import nova.committee.talismans.common.morph.cap.instance.common.CommonCapabilityHandler;
import nova.committee.talismans.common.net.cap.PufferfishPuff;

@EventBusSubscriber
public class PufferfishCapabilityHandler extends CommonCapabilityHandler<IPufferfishCapability, PufferfishPuff.PufferfishPuffPacket>
{
	public static final PufferfishCapabilityHandler INSTANCE = new PufferfishCapabilityHandler();

	public PufferfishCapabilityHandler()
	{
		super(PufferfishCapabilityInstance.PUFFER_CAP);
	}

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event)
	{
		if(event.phase == Phase.END)
		{
			event.player.getCapability(PufferfishCapabilityInstance.PUFFER_CAP).ifPresent(cap ->
			{
				if(cap.getPuffTime() > 0)
					cap.setPuffTime(cap.getPuffTime() - 1);
			});
		}
	}

	public void puffServer(Player player, int duration)
	{
		player.getCapability(PufferfishCapabilityInstance.PUFFER_CAP).ifPresent(cap ->
		{
			cap.puff(duration);

			synchronizeWithClients(player);
		});
	}

	@Override
	protected PufferfishPuff.PufferfishPuffPacket createPacket(Player player, IPufferfishCapability capability)
	{
		return new PufferfishPuff.PufferfishPuffPacket(capability.getOriginalPuffTime(), capability.getPuffTime());
	}
}
