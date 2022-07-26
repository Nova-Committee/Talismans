package nova.committee.talismans.common.morph.cap;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nova.committee.talismans.Static;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/26 12:21
 * Version: 1.0
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ProxyEntityCapabilityInstance implements ICapabilityProvider
{
    public static final ResourceLocation CAPABILITY_NAME = new ResourceLocation(Static.MOD_ID, "proxy_entity_cap");

    public static final Capability<IProxyEntityCapability> PROXY_ENTITY_CAP = CapabilityManager.get(new CapabilityToken<>(){});

    private LazyOptional<IProxyEntityCapability> instance = LazyOptional.of(ProxyEntityCapability::new);

    @SubscribeEvent
    public static void onAttachCapsOnPlayer(AttachCapabilitiesEvent<Entity> event)
    {
        // Only add this on client side
        if(event.getObject().level.isClientSide() && event.getObject() instanceof Entity)
            event.addCapability(CAPABILITY_NAME, new ProxyEntityCapabilityInstance());
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
    {
        return PROXY_ENTITY_CAP.orEmpty(cap, instance);
    }
}
