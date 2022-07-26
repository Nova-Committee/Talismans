package nova.committee.talismans.init.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import nova.committee.talismans.init.event.MotionMultiplierEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Entity.class)
public abstract class MotionMultiplierMixin
{
	@Shadow
	protected Vec3 stuckSpeedMultiplier;

	@Inject(at = @At("HEAD"), method = "makeStuckInBlock(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/phys/Vec3;)V", cancellable = true)
	private void onSetMotionMultiplier(BlockState blockState, Vec3 vector3d, CallbackInfo info)
	{
		Entity thisInstance = ((Entity)(Object)this);
		MotionMultiplierEvent event = new MotionMultiplierEvent(vector3d, thisInstance, blockState, thisInstance.getCommandSenderWorld());

		boolean cancelled = MinecraftForge.EVENT_BUS.post(event);

		if(cancelled)
		{
			info.cancel();
		}
		else if(event.isDirty())
		{
			vector3d = event.getNewMotionMultiplier();
		}
	}
}
