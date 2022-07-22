package nova.committee.talismans.common.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/22 17:30
 * Version: 1.0
 */
public class DragonEmblems extends BaseEmblems{
    public DragonEmblems(){
        setRegistryName("dragon_em");
    }


    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (!pLevel.isClientSide){
            if (!pStack.hasTag()) {
                pStack.getOrCreateTag().putBoolean("cap_on", false);
            }
            var player = ((ServerPlayer) pEntity);
            var nv1 = player.getEffect(MobEffects.FIRE_RESISTANCE);
            if (nv1 == null) {
                nv1 = new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 2400, 2, false, false, false);
            }

            if (pStack.getTag().contains("cap_on")) {
                if (pStack.getTag().getBoolean("cap_on")){

                    player.addEffect(nv1);
                    nv1.duration = 2400;

                }
                else {
                    nv1.duration = 0;
                }

            }
        }
    }

}
