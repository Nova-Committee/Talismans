package nova.committee.talismans.client.morph.handler;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.player.Player;
import nova.committee.talismans.common.morph.cap.instance.pufferfish.PufferfishCapabilityInstance;

public class PufferfishSynchronizer implements IEntitySynchronizer
{
	@Override
	public boolean appliesToMorph(Entity morphEntity)
	{
		return morphEntity instanceof Pufferfish;
	}

	@Override
	public void applyToMorphEntity(Entity morphEntity, Player player)
	{
		Pufferfish pufferEntity = (Pufferfish) morphEntity;

		player.getCapability(PufferfishCapabilityInstance.PUFFER_CAP).ifPresent(cap ->
		{
			pufferEntity.setPuffState(cap.getPuffState());
		});
	}
}
