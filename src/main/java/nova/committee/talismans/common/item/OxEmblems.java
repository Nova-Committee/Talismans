package nova.committee.talismans.common.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/19 16:17
 * Version: 1.0
 */
public class OxEmblems extends BaseEmblems{
    public OxEmblems(){
        setRegistryName("ox_em");
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (!pLevel.isClientSide){
            if (!pStack.hasTag()) {
                pStack.getOrCreateTag().putBoolean("cap_on", false);
            }
            var player = ((ServerPlayer) pEntity);
            var nv1 = player.getEffect(MobEffects.DAMAGE_BOOST);
            var nv2 = player.getEffect(MobEffects.HARM);
            if (nv1 == null) {
                nv1 = new MobEffectInstance(MobEffects.DAMAGE_BOOST, 2400, 1, false, false, false);
            }
            if (nv2 == null) {
                nv2 = new MobEffectInstance(MobEffects.HARM, 2400, 1, false, false, false);
            }
            var effectList = List.of(nv1, nv2);
            if (pStack.getTag().contains("cap_on")) {
                if (pStack.getTag().getBoolean("cap_on")){

                    player.addEffect(nv1);
                    nv1.duration = 2400;

                    player.addEffect(nv2);
                    nv2.duration = 2400;
                }
                else {
                    nv1.duration = 0;
                    nv2.duration = 0;
                }

            }
        }
    }




}
