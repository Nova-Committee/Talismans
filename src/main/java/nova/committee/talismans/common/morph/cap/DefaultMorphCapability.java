package nova.committee.talismans.common.morph.cap;

import com.google.common.collect.Lists;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import nova.committee.talismans.common.morph.MorphItem;
import nova.committee.talismans.common.morph.MorphList;
import nova.committee.talismans.common.net.MorphAddedSynchronizer;
import nova.committee.talismans.common.net.MorphCapabilityFullSynchronizer;
import nova.committee.talismans.common.net.MorphChangedSynchronizer;
import nova.committee.talismans.init.handler.NetworkHandler;

import java.util.Optional;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/26 12:09
 * Version: 1.0
 */
public class DefaultMorphCapability implements IMorphCapability
{
    Player owner;

    boolean mobAttack = false;

    int aggroTimestamp = 0;
    int aggroDuration = 0;

    Optional<MorphItem> morph = Optional.empty();
    Optional<Integer> currentMorphIndex = Optional.empty();

    MorphList morphList = new MorphList();

    public DefaultMorphCapability(Player owner)
    {
        this.owner = owner;
    }

    @Override
    public Player getOwner()
    {
        return owner;
    }

    @Override
    public void syncWithClients()
    {
        if(getOwner().level.isClientSide)
            throw new IllegalAccessError("This method may not be called on client side.");
        else
        {
            NetworkHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(this::getOwner), new MorphCapabilityFullSynchronizer(morph, currentMorphIndex, morphList, getOwner().getUUID()));
        }
    }

    @Override
    public void syncWithClient(ServerPlayer syncTo)
    {
        if(getOwner().level.isClientSide)
            throw new IllegalAccessError("This method may not be called on client side.");
        else
        {
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> syncTo), new MorphCapabilityFullSynchronizer(morph, currentMorphIndex, morphList, getOwner().getUUID()));
        }
    }

    @Override
    public void syncWithConnection(Connection connection)
    {
        if(getOwner().level.isClientSide)
            throw new IllegalAccessError("This method may not be called on client side.");
        else
        {
            NetworkHandler.INSTANCE.send(PacketDistributor.NMLIST.with(() -> Lists.newArrayList(connection)), new MorphCapabilityFullSynchronizer(morph, currentMorphIndex, morphList, getOwner().getUUID()));
        }
    }

    @Override
    public void syncMorphChange()
    {
        if(getOwner().level.isClientSide)
            throw new IllegalAccessError("This method may not be called on client side.");
        else
        {
            // Send the player a packet that may contain morph indices as the player owning this cap has knowledge of those indices
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)getOwner()), new MorphChangedSynchronizer(getOwner().getUUID(), currentMorphIndex, morph));

            // Other players may not have knowledge of those indices, thus we always fully send the current morph
            NetworkHandler.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(this::getOwner), new MorphChangedSynchronizer(getOwner().getUUID(), Optional.empty(), getCurrentMorph()));
        }
    }

    @Override
    public void syncMorphAcquisition(MorphItem item)
    {
        if(getOwner().level.isClientSide)
            throw new IllegalAccessError("This method may not be called on client side.");
        else
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)getOwner()), new MorphAddedSynchronizer(getOwner().getUUID(), item));
    }


    @Override
    public int addToMorphList(MorphItem morphItem)
    {
        return morphList.addToMorphList(morphItem);
    }

    @Override
    public void setMorphList(MorphList list)
    {
        this.morphList = list;

    }

    @Override
    public MorphList getMorphList()
    {
        return morphList;
    }

    @Override
    public void applyHealthOnPlayer()
    {
        // Not really implemented yet...
        float playerHealthPercentage = getOwner().getHealth() / getOwner().getMaxHealth();

        if(!getCurrentMorph().isPresent())
        {
            getOwner().getAttribute(Attributes.MAX_HEALTH).setBaseValue(20);
            getOwner().setHealth(20f * playerHealthPercentage);
        }
        else
        {
            Entity entity = getCurrentMorph().get().createEntity(getOwner().level);

            if(entity instanceof LivingEntity)
            {
                float maxHealthOfEntity = ((LivingEntity)entity).getMaxHealth();
                getOwner().getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHealthOfEntity);
                getOwner().setHealth(maxHealthOfEntity * playerHealthPercentage);
            }
            else
            {
                getOwner().getAttribute(Attributes.MAX_HEALTH).setBaseValue(20);
                getOwner().setHealth(20f * playerHealthPercentage);
            }
        }

    }


    @Override
    public Optional<MorphItem> getCurrentMorphItem()
    {
        return morph;
    }

    @Override
    public Optional<MorphItem> getCurrentMorph()
    {
        if(currentMorphIndex.isPresent())
            return Optional.of(getMorphList().getMorphArrayList().get(currentMorphIndex.get()));
        else if(morph.isPresent())
            return morph;
        else
            return Optional.empty();
    }


    @Override
    public void setMorph(MorphItem morph)
    {
        this.morph = Optional.of(morph);
        this.currentMorphIndex = Optional.empty();
    }

    @Override
    public void demorph()
    {
        this.morph = Optional.empty();
        this.currentMorphIndex = Optional.empty();
    }

    @Override
    public void removePlayerReferences()
    {

    }
    @Override
    public int getLastAggroTimestamp()
    {
        return aggroTimestamp;
    }

    @Override
    public void setLastAggroTimestamp(int timestamp)
    {
        this.aggroTimestamp = timestamp;
    }

    @Override
    public int getLastAggroDuration()
    {
        return aggroDuration;
    }

    @Override
    public void setLastAggroDuration(int aggroDuration)
    {
        this.aggroDuration = aggroDuration;
    }

    @Override
    public boolean shouldMobsAttack()
    {
        return mobAttack;
    }

    @Override
    public void setMobAttack(boolean value)
    {
        this.mobAttack = value;
    }





}
