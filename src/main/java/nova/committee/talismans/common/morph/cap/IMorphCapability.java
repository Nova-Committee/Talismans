package nova.committee.talismans.common.morph.cap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import nova.committee.talismans.common.morph.MorphItem;
import nova.committee.talismans.common.morph.MorphList;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/25 15:54
 * Version: 1.0
 */
public interface IMorphCapability
{
    public Player getOwner();


    /** Returns an optional with the current morph item. This optional will be empty, except when you are morphed with the /morph command. **/
    public Optional<MorphItem> getCurrentMorphItem();

    /** This sort of combines {@link IMorphCapability#getCurrentMorphItem()} by checking if one of these methods doesn't return an empty Optional, and returns the result. **/
    public Optional<MorphItem> getCurrentMorph();

    /** This method adds the given morph item to the morph list and returns the index of the added morph. **/
    public int addToMorphList(MorphItem morphItem);


    /** This method returns the morph list as an object. **/
    public MorphList getMorphList();
    /** This is a setter for the morph list. **/
    public void setMorphList(MorphList list);


    /** This sets the morph item, and its value can be retrieved by invoking {@link IMorphCapability#getCurrentMorphItem()}. **/
    public void setMorph(MorphItem morph);

    /** The purpose of this method is to clear the Optionals holding the current morph data.
     * After calling this method, {@link IMorphCapability#getCurrentMorphItem()}, {@link IMorphCapability#getCurrentMorph()}
     * will return an empty optional.
     **/
    public void demorph();

    public void applyHealthOnPlayer();

    /**
     * By calling this method, you sync the capability data with every player that is tracking this player.
     * This method shall not be called if you intent to try to synchronize a morph
     * change across every client. Use
     * {@link IMorphCapability#syncMorphChange()} to do this.
     **/
    public void syncWithClients();

    /** This method is used to synchronize this capability with a specific target. **/
    public void syncWithClient(ServerPlayer syncTo);

    /** This method is much like the method described above, just with an network manager as a target instead of a player as a target. **/
    public void syncWithConnection(Connection connection);

    /** This method synchronizes a morph change to all players. **/
    public void syncMorphChange();
    /** This method synchronizes the acquisition of a morph to all players. **/
    public void syncMorphAcquisition(MorphItem item);

    /** Returns the value of the flag mentioned in {@link IMorphCapability#setMobAttack(boolean)}. **/
    public boolean shouldMobsAttack();

    /** This method is a flag that indicates whether the mob attack ability is present or not. Note that this value defaults to {@code false}. **/
    public void setMobAttack(boolean value);
    /** Iterates over every current ability and removes references to the player. **/
    public void removePlayerReferences();

    // Aggro timestamps are measured in ints. Aggro timestamp => not saved, aggro duration => saved (indicates how long mobs will be aggro)
    public int getLastAggroTimestamp();
    public void setLastAggroTimestamp(int timestamp);
    public int getLastAggroDuration();
    public void setLastAggroDuration(int aggroDuration);

}
