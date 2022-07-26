package nova.committee.talismans.common.morph.cap.instance.pufferfish;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nova.committee.talismans.Static;
import nova.committee.talismans.common.morph.cap.instance.common.CommonCapabilityInstanceSerializable;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/26 16:17
 * Version: 1.0
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PufferfishCapabilityInstance extends CommonCapabilityInstanceSerializable<IPufferfishCapability>
{
    public static final ResourceLocation CAPABILITY_NAME = new ResourceLocation(Static.MOD_ID, "puffer_cap");

    public static final Capability<IPufferfishCapability> PUFFER_CAP = CapabilityManager.get(new CapabilityToken<>(){});

    public PufferfishCapabilityInstance()
    {
        super(CAPABILITY_NAME, PUFFER_CAP, PufferfishCapability::new);
    }

    @SubscribeEvent
    public static void onAttachCapsOnPlayer(AttachCapabilitiesEvent<Entity> event)
    {
        if(event.getObject() instanceof Player)
            event.addCapability(CAPABILITY_NAME, new PufferfishCapabilityInstance());
    }

    @Override
    public void deserializeAdditional(CompoundTag tag, IPufferfishCapability instance)
    {
        instance.setOriginalPuffTime(tag.getInt("originalPuffTime"));
        instance.setPuffTime(tag.getInt("puffTime"));
    }

    @Override
    public void serializeAdditional(CompoundTag tag, IPufferfishCapability instance)
    {
        tag.putInt("originalPuffTime", instance.getOriginalPuffTime());
        tag.putInt("puffTime", instance.getPuffTime());
    }
}
