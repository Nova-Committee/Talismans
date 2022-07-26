package nova.committee.talismans;

import cn.evolvefield.mods.atomlib.init.proxy.IProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import nova.committee.talismans.client.morph.skin.ShrinkAPIInteractor;
import nova.committee.talismans.common.morph.FallbackMorphItem;
import nova.committee.talismans.common.morph.VisualMorphDataRegistry;
import nova.committee.talismans.init.handler.MorphHandler;
import nova.committee.talismans.init.handler.MorphManagerHandler;
import nova.committee.talismans.init.proxy.ClientProxy;
import nova.committee.talismans.init.proxy.ServerProxy;

@Mod(Static.MOD_ID)
public class Talismans {
    public static IProxy proxy;


    public Talismans() {
        proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);


        var modbus = FMLJavaModLoadingContext.get().getModEventBus();
        modbus.addListener(this::setup);
        var forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(this::onServerStopping);
    }

    private void setup(final FMLCommonSetupEvent event) {

        ShrinkAPIInteractor.init();
        MorphHandler.addMorphItem("fallback_morph_item", FallbackMorphItem::new);
        MorphManagerHandler.registerDefaultManagers();
        Static.VISUAL_MORPH_DATA = new VisualMorphDataRegistry();

    }

    public void onServerStopping(final ServerStoppedEvent event)
    {

        Static.VISUAL_MORPH_DATA.clear();
    }


}
