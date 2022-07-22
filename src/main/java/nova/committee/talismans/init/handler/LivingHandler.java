package nova.committee.talismans.init.handler;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nova.committee.talismans.Static;
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


}
