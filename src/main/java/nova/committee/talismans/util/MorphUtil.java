package nova.committee.talismans.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.DistExecutor;
import nova.committee.talismans.client.morph.handler.IEntitySynchronizer;
import nova.committee.talismans.client.morph.IRenderDataCapability;
import nova.committee.talismans.client.morph.animation.ScaleAnimation;
import nova.committee.talismans.common.morph.MorphItem;
import nova.committee.talismans.common.morph.cap.IMorphCapability;
import nova.committee.talismans.common.morph.cap.MorphCapabilityAttacher;
import nova.committee.talismans.common.morph.cap.RenderDataCapabilityProvider;
import nova.committee.talismans.init.event.PlayerMorphEvent;
import nova.committee.talismans.init.handler.EntitySynchronizerHandler;
import nova.committee.talismans.init.handler.RenderHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/25 15:49
 * Version: 1.0
 */
public class MorphUtil
{
    private static Logger LOGGER = LogManager.getLogger();

    public static void morphToServer(Optional<MorphItem> morphItem, Optional<Integer> morphIndex, Player player)
    {
        morphToServer(morphItem, morphIndex, player, false);
    }

    public static void processCap(Player player, Consumer<IMorphCapability> capConsumer)
    {
        LazyOptional<IMorphCapability> cap = player.getCapability(MorphCapabilityAttacher.MORPH_CAP);

        if(cap.isPresent())
        {
            IMorphCapability resolved = cap.resolve().get();
            capConsumer.accept(resolved);
        }
    }

    public static IMorphCapability getCapOrNull(Player playerEntity)
    {
        if(playerEntity == null)
            return null;

        LazyOptional<IMorphCapability> cap = playerEntity.getCapability(MorphCapabilityAttacher.MORPH_CAP);

        if(cap.isPresent())
            return cap.resolve().get();

        return null;
    }

    /** Method to invoke a morph operation on the server. This will be synced to every player on the server. **/
    public static void morphToServer(Optional<MorphItem> morphItem, Optional<Integer> morphIndex, Player player, boolean force)
    {
        LazyOptional<IMorphCapability> cap = player.getCapability(MorphCapabilityAttacher.MORPH_CAP);

        if(cap.isPresent())
        {
            IMorphCapability resolved = cap.resolve().get();

            MorphItem aboutToMorphTo = null;

            if(morphItem.isPresent())
                aboutToMorphTo = morphItem.get();
            else if(morphIndex.isPresent())
                aboutToMorphTo = resolved.getMorphList().getMorphArrayList().get(morphIndex.get());

            boolean isCanceled = MinecraftForge.EVENT_BUS.post(new PlayerMorphEvent.Server.Pre(player, resolved, aboutToMorphTo));

            if(!(isCanceled && !force))
            {

                if(morphItem.isPresent())
                    resolved.setMorph(morphItem.get());
                else
                    resolved.demorph();

                resolved.syncMorphChange();
                resolved.applyHealthOnPlayer();

                MinecraftForge.EVENT_BUS.post(new PlayerMorphEvent.Server.Post(player, resolved, aboutToMorphTo));
            }
        }
    }

    /** This method is used to invoke functions required for handling a sync on the server on the client side. **/
    public static void morphToClient(Optional<MorphItem> morphItem, Optional<Integer> morphIndex, Player player)
    {
        if(player != null)
        {
            LazyOptional<IMorphCapability> cap = player.getCapability(MorphCapabilityAttacher.MORPH_CAP);

            if(cap.isPresent())
            {
                IMorphCapability resolved = cap.resolve().get();

                MorphItem aboutToMorphTo = null;

                MorphItem oldMorphItem = resolved.getCurrentMorph().orElse(null);

                if(morphItem.isPresent())
                    aboutToMorphTo = morphItem.get();
                else if(morphIndex.isPresent())
                    aboutToMorphTo = resolved.getMorphList().getMorphArrayList().get(morphIndex.get());

                MinecraftForge.EVENT_BUS.post(new PlayerMorphEvent.Client.Pre(player, resolved, aboutToMorphTo));

                IRenderDataCapability renderDataCap = player.getCapability(RenderDataCapabilityProvider.RENDER_CAP).resolve().get();

                Entity cachedEntityOld = renderDataCap.getOrCreateCachedEntity(player);
                ArrayList<IEntitySynchronizer> synchronizersOld = EntitySynchronizerHandler.getSynchronizersForEntity(cachedEntityOld);

                // If we don't have any cached entity => we are demorphed, just use no synchronizer and the player itself as an entity
                if(cachedEntityOld == null)
                {
                    cachedEntityOld = player;
                    synchronizersOld.clear();
                }


                if(morphItem.isPresent())
                    resolved.setMorph(morphItem.get());
                else
                    resolved.demorph();

                MorphItem javaSucks = aboutToMorphTo;


                // Create entity right before we apply the abilities
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> RenderHandler.onBuildNewEntity(player, resolved, javaSucks));


                MinecraftForge.EVENT_BUS.post(new PlayerMorphEvent.Client.Post(player, resolved, aboutToMorphTo));

                Entity cachedEntityNew = renderDataCap.getOrCreateCachedEntity(player);
                ArrayList<IEntitySynchronizer> synchronizersNew = EntitySynchronizerHandler.getSynchronizersForEntity(cachedEntityNew);

                // If we don't have any cached entity => we are demorphed, just use no synchronizer and the player itself as an entity
                if(cachedEntityNew == null)
                {
                    cachedEntityNew = player;
                    synchronizersNew.clear();
                }

                renderDataCap.setAnimation(Optional.of(new ScaleAnimation(player, cachedEntityOld, synchronizersOld, cachedEntityNew, synchronizersNew, 20)));
            }
            else
                System.out.println("Could not synchronize data, as the morph cap is not created yet.");
        }
    }
}
