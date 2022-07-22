package nova.committee.talismans.init.registry;

import net.minecraft.world.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import nova.committee.talismans.Static;
import nova.committee.talismans.common.item.*;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/19 18:09
 * Version: 1.0
 */
@Mod.EventBusSubscriber(modid = Static.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {
    public static Item rat_em;
    public static Item ox_em;
    public static Item rabbit_em;
    public static Item dragon_em;
    public static Item snake_em;
    public static Item horse_em;
    public static Item chicken_em;
    public static Item dog_em;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        final IForgeRegistry<Item> registry = event.getRegistry();

        registry.registerAll(
                rat_em = new RatEmblems(),
                ox_em = new OxEmblems(),
                rabbit_em = new RabbitEmblems(),
                dragon_em = new DragonEmblems(),
                snake_em = new SnakeEmblems(),
                horse_em = new HorseEmblems(),
                chicken_em = new HorseEmblems(),
                dog_em= new DogEmblems()
        );
    }
}
