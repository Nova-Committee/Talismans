package nova.committee.talismans.init.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import nova.committee.talismans.Static;
import nova.committee.talismans.common.buff.SoulCrossEffect;

import static cn.evolvefield.mods.atomlib.init.registry.RegistryUtil.register;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/19 18:32
 * Version: 1.0
 */
@Mod.EventBusSubscriber(modid = Static.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEffects {

    public static ResourceLocation SOUL_CROSS = new ResourceLocation(Static.MOD_ID, "soul_cross");

    public static final MobEffect soulCross = new SoulCrossEffect();


    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<MobEffect> evt) {
        IForgeRegistry<MobEffect> r = evt.getRegistry();
        register(r, SOUL_CROSS, soulCross);

    }
}
