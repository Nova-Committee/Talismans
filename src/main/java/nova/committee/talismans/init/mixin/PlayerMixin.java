package nova.committee.talismans.init.mixin;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import nova.committee.talismans.client.morph.IRenderDataCapability;
import nova.committee.talismans.common.morph.cap.RenderDataCapabilityProvider;
import nova.committee.talismans.util.ProtectedMethodAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(Player.class)
public abstract class PlayerMixin
{
	private static final ProtectedMethodAccess<LivingEntity, SoundEvent> GET_HURT_SOUND = new ProtectedMethodAccess<>(LivingEntity.class, "m_7975_", DamageSource.class);
	private static final ProtectedMethodAccess<LivingEntity, SoundEvent> GET_DRINKING_SOUND = new ProtectedMethodAccess<>(LivingEntity.class, "m_7838_", ItemStack.class);
	private static final ProtectedMethodAccess<LivingEntity, SoundEvent> GET_EATING_SOUND = new ProtectedMethodAccess<>(LivingEntity.class, "m_7866_", ItemStack.class);

	@Inject(at = @At("HEAD"), method = "getHurtSound", cancellable = true)
	private void getHurtSound(DamageSource damageSource, CallbackInfoReturnable<SoundEvent> hurtSound)
	{
		handleSoundReplacement(hurtSound, living -> GET_HURT_SOUND.getValue(living, damageSource));
	}

	private void handleSoundReplacement(CallbackInfoReturnable<SoundEvent> hurtSound, Function<LivingEntity, SoundEvent> soundSupplier)
	{
		Player thisInstance = (Player) ((Object)this);

		if(thisInstance.getLevel().isClientSide())
		{
			LazyOptional<IRenderDataCapability> renderDataOpt = thisInstance.getCapability(RenderDataCapabilityProvider.RENDER_CAP);

			if(renderDataOpt.isPresent())
			{
				IRenderDataCapability cap = renderDataOpt.resolve().get();

				Entity entity = cap.getOrCreateCachedEntity(thisInstance);

				if(entity != null && entity instanceof LivingEntity living)
				{
					hurtSound.setReturnValue(soundSupplier.apply(living));
				}
			}
		}
	}
}
