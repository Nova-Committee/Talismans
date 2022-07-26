package nova.committee.talismans.client.morph.handler;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;
import nova.committee.talismans.common.net.cap.ProxyEntityEvent;
import nova.committee.talismans.init.handler.NetworkHandler;
import nova.committee.talismans.init.registry.ModEntityTypeTags;
import nova.committee.talismans.util.MorphUtil;

@EventBusSubscriber
public class IronGolemSynchronizer
{
	@SubscribeEvent
	public static void onLivingDamage(LivingDamageEvent event)
	{
//		System.out.println("Client Damage event: " + event.getEntity().getLevel().isClientSide());
		if(event.getSource().getEntity() instanceof Player player)
		{
			MorphUtil.processCap(player, cap ->
			{
				cap.getCurrentMorph().ifPresent(currentMorph ->
				{
					if(currentMorph.getEntityType().is(ModEntityTypeTags.IRON_GOLEM_ALIKE))
					{
						// Send byte 4 as an entity event to all nearby players so that the iron golem attack animation gets displayed correctly.
						NetworkHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new ProxyEntityEvent.ProxyEntityEventPacket(player, (byte) 4));
					}
				});
			});
		}
	}
}
