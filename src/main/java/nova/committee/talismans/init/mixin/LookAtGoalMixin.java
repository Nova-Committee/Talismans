package nova.committee.talismans.init.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import nova.committee.talismans.common.morph.cap.IMorphCapability;
import nova.committee.talismans.util.MorphUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LookAtPlayerGoal.class)
public abstract class LookAtGoalMixin extends Goal
{
	@Shadow
	protected Entity lookAt;

	@Inject(method = "canUse()Z", at = @At("TAIL"), cancellable = true)
	private void canUse(CallbackInfoReturnable<Boolean> callback)
	{
		if(this.lookAt != null && this.lookAt instanceof Player)
		{
			// Get capability and check if entity is morphed.
			IMorphCapability cap = MorphUtil.getCapOrNull((Player) this.lookAt);

			if(cap != null)
			{
				callback.setReturnValue(!cap.getCurrentMorph().isPresent());
			}
		}
	}
}
