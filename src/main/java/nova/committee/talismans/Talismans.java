package nova.committee.talismans;

import cn.evolvefield.mods.atomlib.init.proxy.IProxy;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import nova.committee.talismans.init.proxy.ClientProxy;
import nova.committee.talismans.init.proxy.ServerProxy;

@Mod(Static.MOD_ID)
public class Talismans {
    public static IProxy proxy;


    public Talismans() {
        var modbus = FMLJavaModLoadingContext.get().getModEventBus();
        modbus.addListener(this::setup);
        modbus.addListener(this::clientSetup);

        proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
    }

    private void setup(final FMLCommonSetupEvent event) {

    }

    private void clientSetup(final FMLClientSetupEvent event) {
        proxy.init();
    }

}
