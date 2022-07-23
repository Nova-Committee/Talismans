package nova.committee.talismans.common.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/22 17:30
 * Version: 1.0
 */
public class DragonEmblems extends BaseEmblems{
    public DragonEmblems(){
        setRegistryName("dragon_em");
    }


    @Override
    public void inventoryTick(@NotNull ItemStack pStack, Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (!pLevel.isClientSide) {
            var player = ((ServerPlayer) pEntity);
            var nv1 = player.getEffect(MobEffects.FIRE_RESISTANCE);
            if (nv1 == null) {
                nv1 = new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 2400, 2, false, false, false);
            }
            player.addEffect(nv1);
            nv1.duration = 2400;
            player.fireImmune();
            player.clearFire();

        }
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        var stack = pContext.getItemInHand();
        var player = pContext.getPlayer();
        var pos = pContext.getClickedPos();
        var level = pContext.getLevel();
        if (!pContext.getLevel().isClientSide && player != null){
            player.getCooldowns().addCooldown(this, 100);
            var vec3 = player.getViewVector(1.0F);
            double d2 = pos.getX() - (player.getX() + vec3.x * 4.0D);
            double d3 = pos.getY() - (0.5D + player.getY(0.5D));
            double d4 = pos.getZ() - (player.getZ() + vec3.z * 4.0D);
            LargeFireball largeFireball = new LargeFireball(level, player, d2, d3, d4, 2);
            largeFireball.setItem(stack);
            largeFireball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(largeFireball);

            player.awardStat(Stats.ITEM_USED.get(this));
        }
        return super.useOn(pContext);
    }


}
