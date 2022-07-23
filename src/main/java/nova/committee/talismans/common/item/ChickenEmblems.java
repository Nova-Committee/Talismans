package nova.committee.talismans.common.item;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/22 18:13
 * Version: 1.0
 */
public class ChickenEmblems extends BaseEmblems{
    public ChickenEmblems(){
        setRegistryName("chicken_em");
    }



    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        var stack = pPlayer.getItemInHand(pUsedHand);
        var nv1 = pPlayer.getEffect(MobEffects.SLOW_FALLING);
        var nv2 = pPlayer.getEffect(MobEffects.SLOW_FALLING);
        if (nv1 == null) {
            nv1 = new MobEffectInstance(MobEffects.SLOW_FALLING, 1200, 1, false, false, false);
        }
        if (nv2 == null) {
            nv2 = new MobEffectInstance(MobEffects.JUMP, 1200, 1, false, false, false);
        }
        if (!pLevel.isClientSide){
            if (stack.getTag().getBoolean("extra_cap")){

            }
            else {
                pPlayer.getCooldowns().addCooldown(this, 1200);
                pPlayer.addEffect(nv1);
                pPlayer.addEffect(nv2);
            }
            pPlayer.awardStat(Stats.ITEM_USED.get(this));
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }


}
