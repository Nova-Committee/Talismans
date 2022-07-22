package nova.committee.talismans.init.registry;

import net.minecraft.world.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import nova.committee.talismans.Static;
import nova.committee.talismans.common.item.OxEmblems;
import nova.committee.talismans.common.item.RatEmblems;

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

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        final IForgeRegistry<Item> registry = event.getRegistry();

        registry.registerAll(
                rat_em = new RatEmblems(),
                ox_em = new OxEmblems()
        );
    }
}
