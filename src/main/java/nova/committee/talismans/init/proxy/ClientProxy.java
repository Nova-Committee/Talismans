package nova.committee.talismans.init.proxy;

import cn.evolvefield.mods.atomlib.init.proxy.IProxy;
import cn.evolvefield.mods.atomlib.init.registry.RegistryUtil;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import nova.committee.talismans.client.camera.FreeCamera;
import nova.committee.talismans.client.morph.UglyHackThatDoesntWork;
import nova.committee.talismans.client.morph.handler.*;
import nova.committee.talismans.client.particle.LaserParticle;
import nova.committee.talismans.common.morph.player.AdvancedAbstractClientPlayerEntity;
import nova.committee.talismans.init.handler.EntitySynchronizerHandler;
import nova.committee.talismans.init.registry.ModParticleTypes;
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

    public void registerParticleFactories(ParticleFactoryRegisterEvent event) {
        RegistryUtil.registerParticleFactory(ModParticleTypes.laser, LaserParticle.Factory::new);
 }


    private void clientSetup(final FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(toggleFreeCamera);
        EntitySynchronizerHandler.addEntitySynchronizer(new CommonEntitySynchronizer());
        EntitySynchronizerHandler.addEntitySynchronizer(new LivingEntitySynchronzier());
        EntitySynchronizerHandler.addEntitySynchronizer(new SquidSynchronizer());
        EntitySynchronizerHandler.addEntitySynchronizer(new AbstractPlayerSynchronizer());
        EntitySynchronizerHandler.addEntitySynchronizer(new TamableSynchronizer());
        EntitySynchronizerHandler.addEntitySynchronizer(new EndermanSynchronizer());
        EntitySynchronizerHandler.addEntitySynchronizer(new EnderDragonSynchronizer());

        UglyHackThatDoesntWork.thisisstupid = (gameProfile, world) ->
        {
            AdvancedAbstractClientPlayerEntity entity = new AdvancedAbstractClientPlayerEntity((ClientLevel) world, gameProfile);

            Minecraft.getInstance().getSkinManager().registerSkins(gameProfile, (type, resourceLocation, texture) ->
            {
                if(type == MinecraftProfileTexture.Type.CAPE)
                {
                    entity.capeResourceLocation = Minecraft.getInstance().getSkinManager().registerTexture(texture, type);
                }
                else if(type == MinecraftProfileTexture.Type.SKIN)
                {
                    entity.skinResourceLocation = Minecraft.getInstance().getSkinManager().registerTexture(texture, type);

                    String modelName = texture.getMetadata("model");

                    entity.modelName = modelName == null ? "default" : modelName;
                }
                else if(type == MinecraftProfileTexture.Type.ELYTRA)
                {
                    entity.elytraResourceLocation = Minecraft.getInstance().getSkinManager().registerTexture(texture, type);
                }
            }, true);

            return entity;
        };
    }

    @Override
    public void init() {
        var modbus = FMLJavaModLoadingContext.get().getModEventBus();
        modbus.addListener(this::clientSetup);
        modbus.addListener(this::registerParticleFactories);

        var forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(this::onClientTick);
        forgeBus.addListener(this::onRenderTick);
        forgeBus.addListener(this::onWorldUnload);
        forgeBus.addListener(this::onKeyInputEvent);



    }
}
