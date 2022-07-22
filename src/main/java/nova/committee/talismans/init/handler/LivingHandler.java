package nova.committee.talismans.init.handler;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nova.committee.talismans.Static;
import nova.committee.talismans.common.item.DogEmblems;
import nova.committee.talismans.init.registry.ModEffects;
import nova.committee.talismans.init.registry.ModItems;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/19 18:31
 * Version: 1.0
 */
@Mod.EventBusSubscriber(modid = Static.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LivingHandler {


    @SubscribeEvent
    public static void onEntityKill(LivingDeathEvent event) {
        Entity killer = event.getSource().getEntity();
        if (killer instanceof ServerPlayer player &&
                player.getInventory().contains(ModItems.rat_em.getDefaultInstance())) {
            if (player.addEffect(new MobEffectInstance(ModEffects.soulCross))) {
                player.heal(event.getEntityLiving().getMaxHealth() / 20);
            }
        }
    }


    @SubscribeEvent
    public static void onAttacked(LivingAttackEvent event) {
        if (!(event.getEntityLiving() instanceof Player player)) {
            return;
        }
        if (player.getInventory().contains(ModItems.dog_em.getDefaultInstance())) {
            if (player.getInventory().items.stream().allMatch(itemStack -> itemStack.getItem() instanceof DogEmblems
                    && itemStack.getTag().contains("cap_on") && itemStack.getTag().getBoolean("cap_on"))) {

                event.setCanceled(true);
            }

        }
    }


}



