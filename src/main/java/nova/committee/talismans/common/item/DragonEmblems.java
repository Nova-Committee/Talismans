package nova.committee.talismans.common.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        var stack = pPlayer.getItemInHand(pUsedHand);
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
                pPlayer.getCooldowns().addCooldown(this, 100);
                Vec3 vec32 = pPlayer.getViewVector(1.0F);
                double d6 = pPlayer.getX() - vec32.x;
                double d7 = pPlayer.getEyeHeight();
                double d8 = pPlayer.getZ() - vec32.z;
                double range = 60;
                Vec3 look = pPlayer.getLookAngle();
                Vec3 to = new Vec3(pPlayer.getX() + look.x * range, pPlayer.getY() + pPlayer.getEyeHeight() + look.y * range, pPlayer.getZ() + look.z * range);

                double d9 = to.x() - d6;
                double d10 = to.y() - d7;
                double d11 = to.z() - d8;
                DragonFireball largeFireball = new DragonFireball(pLevel, pPlayer, d9, d10, d11);
                largeFireball.moveTo(d6, d7, d8, 0.0F, 0.0F);//wow
                pLevel.addFreshEntity(largeFireball);
            }

            pPlayer.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.consume(stack);

        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }



}
