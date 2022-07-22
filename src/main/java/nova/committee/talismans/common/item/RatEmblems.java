package nova.committee.talismans.common.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/19 15:20
 * Version: 1.0
 */
public class RatEmblems extends BaseEmblems{

    public RatEmblems(){
        setRegistryName("rat_em");
    }

    @Override
    public void inventoryTick(@NotNull ItemStack pStack, Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (!pLevel.isClientSide){
            if (!pStack.hasTag()) {
                pStack.getOrCreateTag().putBoolean("cap_on", false);
            }
            var player = ((ServerPlayer) pEntity);
            var nv1 = player.getEffect(MobEffects.HEALTH_BOOST);
            var nv2 = player.getEffect(MobEffects.REGENERATION);
            var nv3 = player.getEffect(MobEffects.HEAL);

            if (nv1 == null) {
                nv1 = new MobEffectInstance(MobEffects.HEALTH_BOOST, 2400, 1, false, false, false);
            }
            if (nv2 == null) {
                nv2 = new MobEffectInstance(MobEffects.REGENERATION, 2400, 1, false, false, false);
            }
            if (nv3 == null) {
                nv3 = new MobEffectInstance(MobEffects.HEAL, 2400, 1, false, false, false);
            }
            if (pStack.getTag().contains("cap_on")) {
                if (pStack.getTag().getBoolean("cap_on")){

                    player.addEffect(nv1);
                    nv1.duration = 2400;

                    player.addEffect(nv2);
                    nv2.duration = 2400;

                    player.addEffect(nv3);
                    nv3.duration = 2400;
                }
                else {
                    nv1.duration = 0;
                    nv2.duration = 0;
                    nv3.duration = 0;
                }

            }
        }
    }

}
