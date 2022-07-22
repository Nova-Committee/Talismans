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
 * Date: 2022/7/22 18:13
 * Version: 1.0
 */
public class ChickenEmblems extends BaseEmblems{
    public ChickenEmblems(){
        setRegistryName("chicken_em");
    }


    @Override
    public void inventoryTick(@NotNull ItemStack pStack, Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (!pLevel.isClientSide){
            if (!pStack.hasTag()) {
                pStack.getOrCreateTag().putBoolean("cap_on", false);
            }
            var player = ((ServerPlayer) pEntity);
            var nv1 = player.getEffect(MobEffects.SLOW_FALLING);

            if (nv1 == null) {
                nv1 = new MobEffectInstance(MobEffects.SLOW_FALLING, 2400, 0, false, false, false);
            }
            if (pStack.getTag().contains("cap_on")) {
                if (pStack.getTag().getBoolean("cap_on")){

                    player.addEffect(nv1);
                    nv1.duration = 2400;

                    player.getAbilities().mayfly = true;
                    //player.getAbilities().flying = true;
                }
                else {
                    nv1.duration = 0;

                    player.getAbilities().mayfly = false;
                    player.getAbilities().flying = false;
                }

            }
        }
    }
}
