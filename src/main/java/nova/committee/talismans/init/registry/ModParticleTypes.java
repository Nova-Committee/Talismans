package nova.committee.talismans.init.registry;

import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import nova.committee.talismans.Static;
import nova.committee.talismans.common.laser.particle.LaserParticleData;
import nova.committee.talismans.common.laser.particle.LaserParticleType;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/27 14:26
 * Version: 1.0
 */
@Mod.EventBusSubscriber(modid = Static.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModParticleTypes {

    public static ParticleType<LaserParticleData> laser;


    @SubscribeEvent
    public static void registerParticles(RegistryEvent.Register<ParticleType<?>> event) {
        final IForgeRegistry<ParticleType<?>> registry = event.getRegistry();

        registry.registerAll(
                laser = new LaserParticleType()

        );
    }
}
