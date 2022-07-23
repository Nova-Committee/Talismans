package nova.committee.talismans.common.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
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
    public void inventoryTick(@NotNull ItemStack pStack, Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (!pLevel.isClientSide){

            var player = ((ServerPlayer) pEntity);

            if (pStack.getTag().contains("extra_cap")) {
                if (pStack.getTag().getBoolean("extra_cap")){
                    player.getCooldowns().addCooldown(this, 2400);

                    CriteriaTriggers.USED_TOTEM.trigger(player, pStack);
                    pLevel.broadcastEntityEvent(player, (byte)35);

                }
                player.awardStat(Stats.ITEM_USED.get(this));

            }
        }
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
    }

}
