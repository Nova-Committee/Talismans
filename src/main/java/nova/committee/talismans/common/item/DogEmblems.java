package nova.committee.talismans.common.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/22 18:17
 * Version: 1.0
 */
public class DogEmblems extends BaseEmblems{
    public DogEmblems(){
        setRegistryName("dog_em");
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        super.use(pLevel, pPlayer, pUsedHand);
        var stack = pPlayer.getItemInHand(pUsedHand);

        if (!pLevel.isClientSide){
            var player = ((ServerPlayer) pPlayer);

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

                CriteriaTriggers.USED_TOTEM.trigger(player, stack);
                pLevel.broadcastEntityEvent(pPlayer, (byte)35);
            }

            pPlayer.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.consume(stack);
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }


}
