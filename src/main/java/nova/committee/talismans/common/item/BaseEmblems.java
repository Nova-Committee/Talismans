package nova.committee.talismans.common.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import nova.committee.talismans.Static;
import org.jetbrains.annotations.NotNull;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/19 16:10
 * Version: 1.0
 */
public abstract class BaseEmblems extends Item {
    public BaseEmblems() {
        super(new Properties()
                .tab(Static.TAB)
                .stacksTo(1)
                .fireResistant()
                .rarity(Static.RARITY)
                .setNoRepair()

        );
    }

    @Override
    public boolean isFoil(@NotNull ItemStack pStack) {
        return true;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (!pLevel.isClientSide && pPlayer.isCrouching()) {
            var stack = pPlayer.getItemInHand(pUsedHand);
            if(stack.hasTag() && stack.getTag().contains("cap_on")){
                stack.getOrCreateTag().putBoolean("cap_on", !stack.getTag().getBoolean("cap_on"));
            }
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
