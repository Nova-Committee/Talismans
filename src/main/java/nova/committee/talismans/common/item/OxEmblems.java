package nova.committee.talismans.common.item;

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
 * Date: 2022/7/19 16:17
 * Version: 1.0
 */
public class OxEmblems extends BaseEmblems{
    public OxEmblems(){
        setRegistryName("ox_em");
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        var stack = pPlayer.getItemInHand(pUsedHand);
        if (!pLevel.isClientSide){
            var nv1 = pPlayer.getEffect(MobEffects.DAMAGE_BOOST);
            if (nv1 == null) {
                nv1 = new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1200, 1, false, false, false);
            }
            pPlayer.addEffect(nv1);
            pPlayer.getCooldowns().addCooldown(stack.getItem(), 1200);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }



}
