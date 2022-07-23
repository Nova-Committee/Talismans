package nova.committee.talismans.common.item;

import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
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

    private boolean foil = false;
    public BaseEmblems() {
        super(new Properties()
                .tab(Static.TAB)
                .stacksTo(1)
                .fireResistant()
                .rarity(Static.RARITY)
                .setNoRepair()

        );
    }

    public void setFoil(boolean foil) {
        this.foil = foil;
    }

    @Override
    public boolean isFoil(@NotNull ItemStack pStack) {
        return foil;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack pStack, Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (!pLevel.isClientSide) {
            if (!pStack.hasTag()) {
                pStack.getOrCreateTag().putBoolean("extra_cap", false);
            }
        }
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
    }
}
