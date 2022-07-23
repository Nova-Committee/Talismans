package nova.committee.talismans.common.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/22 18:06
 * Version: 1.0
 */
public class HorseEmblems extends BaseEmblems{
    public HorseEmblems(){
        setRegistryName("horse_em");
    }


    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        super.use(pLevel, pPlayer, pUsedHand);
        var stack = pPlayer.getItemInHand(pUsedHand);
        var nv1 = pPlayer.getEffect(MobEffects.HEAL);
        if (nv1 == null) {
            nv1 = new MobEffectInstance(MobEffects.HEAL, 600, 2, false, false, false);
        }
        if (!pLevel.isClientSide){
            if (pPlayer.isCrouching() && stack.hasTag() && stack.getTag().contains("extra_cap")) {
                if(stack.getTag().getBoolean("extra_cap")){
                    stack.getOrCreateTag().putBoolean("extra_cap",false);
                    setFoil(false);
                }
                else {
                    stack.getOrCreateTag().putBoolean("extra_cap",true);
                    setFoil(true);
                }
                return InteractionResultHolder.consume(stack);
            }
            if (stack.getTag().getBoolean("extra_cap")){
                pPlayer.getCooldowns().addCooldown(this, 2400);
                pPlayer.getActiveEffects().forEach(mobEffectInstance -> {
                    if (!mobEffectInstance.getEffect().isBeneficial())
                        pPlayer.removeEffect(mobEffectInstance.getEffect());
                });
            }
            else {
                pPlayer.getCooldowns().addCooldown(this, 1200);
                pPlayer.addEffect(nv1);
            }
            pPlayer.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.consume(stack);
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }

}
