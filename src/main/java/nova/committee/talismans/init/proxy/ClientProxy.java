package nova.committee.talismans.init.proxy;

import cn.evolvefield.mods.atomlib.init.proxy.IProxy;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import nova.committee.talismans.client.camera.FreeCamera;
import org.lwjgl.glfw.GLFW;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/23 23:32
 * Version: 1.0
 */
public class ClientProxy implements IProxy {

    public KeyMapping toggleFreeCamera = new KeyMapping("key.talismans.freecam.toggle",GLFW.GLFW_KEY_F6, "category.talismans");;


    public void onKeyInputEvent(InputEvent.KeyInputEvent event) {
        FreeCamera.instance.onKeyInput();
    }

    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            FreeCamera.instance.onRenderTickStart();
        }
    }

    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            FreeCamera.instance.onClientTickStart();
        }
    }

    public void onWorldUnload(WorldEvent.Unload event) {
        FreeCamera.instance.onWorldUnload();
    }

    @Override
    public void init() {
        ClientRegistry.registerKeyBinding(toggleFreeCamera);

        var forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(this::onClientTick);
        forgeBus.addListener(this::onRenderTick);
        forgeBus.addListener(this::onWorldUnload);
        forgeBus.addListener(this::onKeyInputEvent);
    }
}
