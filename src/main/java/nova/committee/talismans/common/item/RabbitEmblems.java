package nova.committee.talismans.common.item;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/22 17:27
 * Version: 1.0
 */
public class RabbitEmblems extends BaseEmblems{
    public RabbitEmblems(){
        setRegistryName("rabbit_em");
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        var stack = pPlayer.getItemInHand(pUsedHand);
        var nv1 = pPlayer.getEffect(MobEffects.MOVEMENT_SPEED);
        if (nv1 == null) {
            nv1 = new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 1, false, false, false);
        }
        if (!pLevel.isClientSide){
            if (stack.getTag().getBoolean("extra_cap")){
                //pPlayer.removeEffect(nv1.getEffect());
                pPlayer.getCooldowns().addCooldown(this, 100);
                ThrownEnderpearl thrownenderpearl = new ThrownEnderpearl(pLevel, pPlayer);
                thrownenderpearl.setItem(stack);
                thrownenderpearl.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 1.5F, 1.0F);
                pLevel.addFreshEntity(thrownenderpearl);
            }
            else {
                pPlayer.getCooldowns().addCooldown(this, 1200);
                pPlayer.addEffect(nv1);
            }
            pPlayer.awardStat(Stats.ITEM_USED.get(this));
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
