package nova.committee.talismans.init.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;
import nova.committee.talismans.MixinStatic;
import nova.committee.talismans.client.camera.FreeCamera;
import nova.committee.talismans.client.morph.IRenderDataCapability;
import nova.committee.talismans.common.morph.cap.ProxyEntityCapabilityInstance;
import nova.committee.talismans.common.morph.cap.RenderDataCapabilityProvider;
import nova.committee.talismans.util.ProtectedMethodAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/23 23:42
 * Version: 1.0
 */

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow(aliases = "Lnet/minecraft/world/entity/Entity;calculateViewVector(FF)Lnet/minecraft/world/phys/Vec3;")
    protected abstract Vec3 calculateViewVector(float p_20172_, float p_20173_);

    @Inject(at = @At("HEAD"), method = "turn(DD)V", cancellable = true)
    private void onTurn(double yRot, double xRot, CallbackInfo info) {
        if (!MixinStatic.insideTurnPlayer) {
            return;
        }
        if (!FreeCamera.instance.isActive()) {
            return;
        }
        var entity = (Entity) (Object) this;
        if (entity instanceof LocalPlayer) {
            FreeCamera.instance.onMouseTurn(yRot, xRot);
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "getEyePosition(F)Lnet/minecraft/world/phys/Vec3;", cancellable = true)
    private void onGetEyePosition(float p_20300_, CallbackInfoReturnable<Vec3> info) {
        FreeCamera freeCam = FreeCamera.instance;
        if (freeCam.isActive() && freeCam.shouldOverridePlayerPosition()) {
            var entity = (Entity) (Object) this;
            if (entity instanceof LocalPlayer) {
                info.setReturnValue(new Vec3(freeCam.getX(), freeCam.getY(), freeCam.getZ()));
                info.cancel();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "getViewVector(F)Lnet/minecraft/world/phys/Vec3;", cancellable = true)
    private void onGetViewVector(float p_20253_, CallbackInfoReturnable<Vec3> info) {
        FreeCamera freeCam = FreeCamera.instance;
        if (freeCam.isActive() && freeCam.shouldOverridePlayerPosition()) {
            var entity = (Entity) (Object) this;
            if (entity instanceof LocalPlayer) {
                info.setReturnValue(this.calculateViewVector(freeCam.getXRot(), freeCam.getYRot()));
                info.cancel();
            }
        }
    }

    private static final ProtectedMethodAccess<Entity, SoundEvent> PLAY_STEP_SOUND = new ProtectedMethodAccess<>(Entity.class, "m_7355_", BlockPos.class, BlockState.class);

    @Inject(at = @At("HEAD"), method = "playSound")
    public void playSound(SoundEvent pSound, float pVolume, float pPitch, CallbackInfo callbackInfo)
    {
        Entity thisInstance = (Entity) ((Object)this);

        if(thisInstance.getLevel().isClientSide() && !thisInstance.isSilent())
        {
            thisInstance.getCapability(ProxyEntityCapabilityInstance.PROXY_ENTITY_CAP).ifPresent(cap ->
            {
                if(cap.isProxyEntity())
                {
                    thisInstance.level.playSound(Minecraft.getInstance().player, thisInstance.getX(), thisInstance.getY(), thisInstance.getZ(), pSound, thisInstance.getSoundSource(), pVolume, pPitch);
                }
            });
        }
    }

//    @Inject(at = @At("HEAD"), method = "playStepSound", cancellable = true)
//    public void playStepSound(BlockPos blockPos, BlockState block, CallbackInfo callbackInfo)
//    {
//        Entity thisInstance = (Entity) ((Object)this);
//
//        if(thisInstance.getLevel().isClientSide() && thisInstance instanceof Player player)
//        {
//            LazyOptional<IRenderDataCapability> renderDataOpt = player.getCapability(RenderDataCapabilityProvider.RENDER_CAP);
//
//            if(renderDataOpt.isPresent())
//            {
//                IRenderDataCapability cap = renderDataOpt.resolve().get();
//
//                Entity entity = cap.getOrCreateCachedEntity(player);
//                PLAY_STEP_SOUND.getValue(entity, blockPos, block);
//                callbackInfo.cancel();
//            }
//        }
//    }
}
