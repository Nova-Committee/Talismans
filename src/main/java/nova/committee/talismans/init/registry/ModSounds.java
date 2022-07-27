package nova.committee.talismans.init.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import nova.committee.talismans.Static;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/27 15:26
 * Version: 1.0
 */
@Mod.EventBusSubscriber(modid = Static.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSounds {

    public static SoundEvent laser;
    public static SoundEvent laser_start;
    public static SoundEvent laser_stop;

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        final IForgeRegistry<SoundEvent> registry = event.getRegistry();

        registry.registerAll(
                laser = new SoundEvent(new ResourceLocation(Static.MOD_ID, "laser")).setRegistryName("laser"),
                laser_start = new SoundEvent(new ResourceLocation(Static.MOD_ID, "laser_start")).setRegistryName("laser_start"),
                laser_stop = new SoundEvent(new ResourceLocation(Static.MOD_ID, "laser_stop")).setRegistryName("laser_stop")

        );
    }
}
