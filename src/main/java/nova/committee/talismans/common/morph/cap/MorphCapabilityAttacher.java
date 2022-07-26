package nova.committee.talismans.common.morph.cap;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import nova.committee.talismans.Static;
import nova.committee.talismans.init.handler.MorphHandler;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/26 12:08
 * Version: 1.0
 */
@Mod.EventBusSubscriber
public class MorphCapabilityAttacher implements ICapabilitySerializable<CompoundTag>
{
    public static final ResourceLocation CAPABILITY_NAME = new ResourceLocation(Static.MOD_ID, "morph_cap");

    public static final Capability<IMorphCapability> MORPH_CAP = CapabilityManager.get(new CapabilityToken<>(){});

    private Player owner;

    public MorphCapabilityAttacher(Player owner)
    {
        this.owner = owner;
    }

    @SubscribeEvent
    public static void onAttachCapsOnPlayer(AttachCapabilitiesEvent<Entity> event)
    {
        if(event.getObject() instanceof Player)
            event.addCapability(CAPABILITY_NAME, new MorphCapabilityAttacher((Player) event.getObject()));
    }
    LazyOptional<IMorphCapability> cap = LazyOptional.of(() -> new DefaultMorphCapability(owner));

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
    {
        return MORPH_CAP.orEmpty(cap, this.cap);
    }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag capTag = new CompoundTag();

        IMorphCapability instance = cap.resolve().get();

        if(instance.getCurrentMorphItem().isPresent())
            capTag.put("currentMorphItem", instance.getCurrentMorphItem().get().serialize());

        capTag.put("morphList", instance.getMorphList().serializeNBT());


        capTag.putInt("aggroDuration", Math.max(0, instance.getLastAggroDuration() - (owner.getLevel().getServer().getTickCount() - instance.getLastAggroTimestamp())));


        return capTag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        CompoundTag capTag = nbt;

        IMorphCapability instance = cap.resolve().get();

        boolean hasItem = capTag.contains("currentMorphItem");
        boolean hasIndex = capTag.contains("currentMorphIndex");

        if(hasItem)
        {
            instance.setMorph(MorphHandler.deserializeMorphItem(capTag.getCompound("currentMorphItem")));
        }


        instance.getMorphList().deserializeNBT(capTag.getCompound("morphList"));


        instance.setLastAggroDuration(capTag.getInt("aggroDuration"));

    }
}
