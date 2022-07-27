package nova.committee.talismans.init.handler;

import cn.evolvefield.mods.atomlib.common.net.IPacket;
import cn.evolvefield.mods.atomlib.init.handler.NetBaseHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import nova.committee.talismans.Static;
import nova.committee.talismans.common.net.LaserHitBlockPacket;
import nova.committee.talismans.common.net.MorphAddedSynchronizer;
import nova.committee.talismans.common.net.MorphCapabilityFullSynchronizer;
import nova.committee.talismans.common.net.MorphChangedSynchronizer;
import nova.committee.talismans.common.net.cap.*;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/2 13:07
 * Version: 1.0
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class NetworkHandler {
    public static final String PROTOCOL_VERSION = "5";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Static.MOD_ID, "main"),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    private static int id = 0;
    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            registerSimpleImplPacket(MorphCapabilityFullSynchronizer.class, new MorphCapabilityFullSynchronizer());
            registerSimpleImplPacket(MorphAddedSynchronizer.class, new MorphAddedSynchronizer());
            registerSimpleImplPacket(MorphChangedSynchronizer.class, new MorphChangedSynchronizer());
            registerSimpleImplPacket(PufferfishPuff.PufferfishPuffPacket.class, new PufferfishPuff());
            registerSimpleImplPacket(EntityMovementChanged.EntityMovementChangedPacket.class, new EntityMovementChanged());
            registerSimpleImplPacket(EvokerSpell.EvokerSpellPacket.class, new EvokerSpell());
            registerSimpleImplPacket(Flight.FlightPacket.class, new Flight());
            registerSimpleImplPacket(ProxyEntityEvent.ProxyEntityEventPacket.class, new ProxyEntityEvent());
            registerSimpleImplPacket(SquidBoost.SquidBoostPacket.class, new SquidBoost());
            registerSimpleImplPacket(LaserHitBlockPacket.class, new LaserHitBlockPacket());
        });

    }

    public static <T> void registerSimpleImplPacket(Class<T> packetClass, IPacket<T> packet)
    {
        INSTANCE.registerMessage(id++, packetClass, packet::write, packet::read, packet::run);
    }
}
