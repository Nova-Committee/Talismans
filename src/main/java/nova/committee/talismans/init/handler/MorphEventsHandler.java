package nova.committee.talismans.init.handler;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import nova.committee.talismans.Static;
import nova.committee.talismans.client.morph.IRenderDataCapability;
import nova.committee.talismans.client.morph.skin.ShrinkAPIInteractor;
import nova.committee.talismans.common.morph.MorphItem;
import nova.committee.talismans.common.morph.cap.IMorphCapability;
import nova.committee.talismans.common.morph.cap.IProxyEntityCapability;
import nova.committee.talismans.common.morph.cap.MorphCapabilityAttacher;
import nova.committee.talismans.common.morph.cap.RenderDataCapabilityProvider;
import nova.committee.talismans.common.morph.cap.instance.bossbar.BossbarCapabilityInstance;
import nova.committee.talismans.common.morph.cap.instance.bossbar.IBossbarCapability;
import nova.committee.talismans.common.morph.cap.instance.evoker.EvokerSpellCapabilityHandler;
import nova.committee.talismans.common.morph.cap.instance.evoker.IEvokerSpellCapability;
import nova.committee.talismans.common.morph.cap.instance.pufferfish.IPufferfishCapability;
import nova.committee.talismans.common.morph.cap.instance.pufferfish.PufferfishCapabilityHandler;
import nova.committee.talismans.init.event.MorphCreatedFromEntityEvent;
import nova.committee.talismans.init.event.PlayerMorphEvent;
import nova.committee.talismans.util.EntityClassByTypeCache;
import nova.committee.talismans.util.MorphUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/26 13:45
 * Version: 1.0
 */
@Mod.EventBusSubscriber
public class MorphEventsHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    public static int AGGRO_TICKS_TO_PASS = 200;

    // This field indicates whether we should resolve the ability names or not


    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event)
    {
        event.register(IMorphCapability.class);
        event.register(IPufferfishCapability.class);
        event.register(IRenderDataCapability.class);
        event.register(IBossbarCapability.class);
        event.register(IEvokerSpellCapability.class);
        event.register(IProxyEntityCapability.class);
    }

    @SubscribeEvent
    public static void onDatapackSyncing(OnDatapackSyncEvent event)
    {
//        if(event.getPlayer() == null)
//        {
//            Static.VISUAL_MORPH_DATA.syncWithClients();
//
//        }
//        else
//        {
//
//            Static.VISUAL_MORPH_DATA.syncWithClient(event.getPlayer());
//        }
    }

    @SubscribeEvent
    public static void onEntityCreated(EntityJoinWorldEvent event)
    {
        if(event.getWorld().isClientSide())
            return;

        if(event.getEntity() instanceof IronGolem ironGolem)
        {
            ironGolem.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(ironGolem, Player.class, 5, false, false, player ->
            {
                IMorphCapability cap = MorphUtil.getCapOrNull((Player) player);

                if(cap != null)
                {
                    Optional<MorphItem> morphItem = cap.getCurrentMorph();

                    if(morphItem.isPresent())
                    {
                        MorphItem morphItemResolved = morphItem.get();

                        // We should probably ignore players when accessing the cache.
                        if(morphItemResolved.getEntityType() == EntityType.PLAYER || morphItemResolved.getEntityType() == EntityType.CREEPER)
                            return false;

                        Class<? extends Entity> entityClass = EntityClassByTypeCache.getClassForEntityType(morphItemResolved.getEntityType());
                        return Enemy.class.isAssignableFrom(entityClass);
                    }
                }

                return false;
            }));
        }
    }

    @SubscribeEvent
    public static void onPlayerJoined(PlayerEvent.PlayerLoggedInEvent event)
    {
        if(!event.getEntity().level.isClientSide())
        {
            Player player = event.getPlayer();

            LazyOptional<IMorphCapability> cap = player.getCapability(MorphCapabilityAttacher.MORPH_CAP);

            if(cap.isPresent())
            {
                MinecraftForge.EVENT_BUS.post(new PlayerMorphEvent.Server.Pre(player, cap.resolve().get(), cap.resolve().get().getCurrentMorph().orElse(null)));

                cap.resolve().get().syncWithClients();
                cap.resolve().get().applyHealthOnPlayer();

                MinecraftForge.EVENT_BUS.post(new PlayerMorphEvent.Server.Post(player, cap.resolve().get(), cap.resolve().get().getCurrentMorph().orElse(null)));
            }
            PufferfishCapabilityHandler.INSTANCE.synchronizeWithClients(player);
            EvokerSpellCapabilityHandler.INSTANCE.synchronizeWithClients(player);

            showBossbarToEveryoneTrackingPlayer(player);

        }
    }
    public static void showBossbarToEveryoneTrackingPlayer(Player player)
    {
        player.getCapability(BossbarCapabilityInstance.BOSSBAR_CAP).ifPresent(bossbarCap ->
        {
            bossbarCap.getBossbar().ifPresent(bossbar ->
            {
                getPlayersTrackingEntityAndSelf((ServerPlayer) player).forEach(trackingPlayer -> bossbar.addPlayer(trackingPlayer));
            });
        });
    }

    public static List<ServerPlayer> getPlayersTrackingEntity(ServerPlayer player)
    {
        ServerChunkCache chunkCache = (ServerChunkCache) player.level.getChunkSource();

        List<ServerPlayer> serverPlayers = new ArrayList<>();
        Set<ServerPlayerConnection> trackedPlayers = chunkCache.chunkMap.entityMap.get(player.getId()).seenBy;

        for(ServerPlayerConnection connection : trackedPlayers)
            serverPlayers.add(connection.getPlayer());

        return serverPlayers;
    }

    public static List<ServerPlayer> getPlayersTrackingEntityAndSelf(ServerPlayer player)
    {
        List<ServerPlayer> list = getPlayersTrackingEntity(player);
        list.add(player);

        return list;
    }


    @SubscribeEvent
    public static void onPlayerLeft(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if(!event.getPlayer().level.isClientSide())
        {
            event.getPlayer().getCapability(BossbarCapabilityInstance.BOSSBAR_CAP).ifPresent(IBossbarCapability::clearBossbar);
        }
    }

    @SubscribeEvent
    public static void onEntityLeftWorld(EntityLeaveWorldEvent event)
    {
        if(event.getEntity() instanceof Player player)
        {
            if(player.isRemoved() && player.getRemovalReason() == Entity.RemovalReason.UNLOADED_WITH_PLAYER)
            {
                MorphUtil.processCap(player, IMorphCapability::removePlayerReferences);
            }

            if(event.getEntity().getRemovalReason() != Entity.RemovalReason.CHANGED_DIMENSION && event.getEntity().level.isClientSide())
                player.getCapability(RenderDataCapabilityProvider.RENDER_CAP).ifPresent(IRenderDataCapability::invalidateCache);
        }
    }



    @SubscribeEvent
    public static void onMorphCreatedFromEntity(MorphCreatedFromEntityEvent event)
    {

    }

    @SubscribeEvent
    public static void onPlayerIsBeingLoaded(PlayerEvent.StartTracking event)
    {
        if(event.getTarget() instanceof Player player)
        {
            MorphUtil.processCap(player, resolved -> resolved.syncWithClient((ServerPlayer) event.getPlayer()));
            PufferfishCapabilityHandler.INSTANCE.synchronizeWithClient(player, (ServerPlayer) event.getPlayer());
            EvokerSpellCapabilityHandler.INSTANCE.synchronizeWithClient(player, (ServerPlayer) event.getPlayer());


            event.getTarget().getCapability(BossbarCapabilityInstance.BOSSBAR_CAP).ifPresent(bossbarCap ->
            {
                bossbarCap.getBossbar().ifPresent(bossbar -> bossbar.addPlayer((ServerPlayer) event.getPlayer()));
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerStoppedBeingLoaded(PlayerEvent.StopTracking event)
    {

        if(event.getTarget() instanceof Player player)
        {
            event.getTarget().getCapability(BossbarCapabilityInstance.BOSSBAR_CAP).ifPresent(bossbarCap ->
            {
                bossbarCap.getBossbar().ifPresent(bossbar -> bossbar.removePlayer((ServerPlayer) event.getPlayer()));
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerBreakingBlockCheck(PlayerEvent.HarvestCheck event)
    {
        if(!event.getPlayer().isCreative() && event.getTargetBlock().requiresCorrectToolForDrops())
            event.setCanHarvest(false);
    }

    @SubscribeEvent
    public static void onPlayerBreakingBlockSpeed(PlayerEvent.BreakSpeed event)
    {
        if(!event.getPlayer().isCreative() &&  event.getPlayer().getMainHandItem().getItem() instanceof TieredItem)
            event.setNewSpeed(event.getOriginalSpeed() / ((TieredItem)event.getPlayer().getMainHandItem().getItem()).getTier().getSpeed());
    }

    @SubscribeEvent
    public static void onPlayerInteractItem(PlayerInteractEvent.RightClickItem event)
    {
        if(!event.getPlayer().isCreative())
        {
            if(event.getItemStack().getItem() instanceof TieredItem || event.getItemStack().getItem() instanceof ProjectileWeaponItem)
                event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onInteractAtBlock(PlayerInteractEvent.RightClickBlock event)
    {
        if(!event.getPlayer().isCreative() && !(event.getItemStack().getItem() instanceof BlockItem))
        {
            event.setUseItem(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onPlayerKilledLivingEntity(LivingDeathEvent event)
    {
        if(!event.getEntity().level.isClientSide)
        {
            if(event.getSource().getEntity() instanceof Player)
            {
                Player player = (Player) event.getSource().getEntity();

                boolean selfKill = event.getSource().getEntity() == event.getEntity();

                if(!(player instanceof FakePlayer || selfKill))
                {
                    LazyOptional<IMorphCapability> playerMorph = player.getCapability(MorphCapabilityAttacher.MORPH_CAP);

                    if(playerMorph.isPresent())
                    {
                        MorphItem morphItem = MorphManagerHandler.createMorphFromDeadEntity(event.getEntity());

                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        if(!event.getEntity().level.isClientSide)
        {
            MorphUtil.processCap(event.getPlayer(), IMorphCapability::syncWithClients);
        }
    }

    @SubscribeEvent
    public static void onClonePlayer(PlayerEvent.Clone event)
    {
        // I've tested it and it doesnt cause a crash. That's good.
        if(!(event.isWasDeath()))
        {
            event.getOriginal().reviveCaps();

            Optional<IMorphCapability> oldCap = event.getOriginal().getCapability(MorphCapabilityAttacher.MORPH_CAP).resolve();
            Optional<IMorphCapability> newCap = event.getPlayer().getCapability(MorphCapabilityAttacher.MORPH_CAP).resolve();

            event.getOriginal().invalidateCaps();

            if(oldCap.isPresent() && newCap.isPresent())
            {
                IMorphCapability oldResolved = oldCap.get();
                IMorphCapability newResolved = newCap.get();

                newResolved.setMorphList(oldResolved.getMorphList());

                MinecraftForge.EVENT_BUS.post(new PlayerMorphEvent.Server.Pre(event.getPlayer(), newResolved, newResolved.getCurrentMorph().orElse(null)));

                oldResolved.getCurrentMorphItem().ifPresent(morph -> newResolved.setMorph(morph));

                MinecraftForge.EVENT_BUS.post(new PlayerMorphEvent.Server.Post(event.getPlayer(), newResolved, newResolved.getCurrentMorph().orElse(null)));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawnedEvent(PlayerEvent.PlayerRespawnEvent event)
    {
        if(!event.getPlayer().level.isClientSide)
        {
            LazyOptional<IMorphCapability> cap = event.getPlayer().getCapability(MorphCapabilityAttacher.MORPH_CAP);

            if(cap.isPresent())
            {
                IMorphCapability resolved = cap.resolve().get();

                resolved.syncWithClients();
                resolved.applyHealthOnPlayer();
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDeathEvent(LivingDeathEvent event)
    {
        if(event.getEntityLiving() instanceof Player player)
        {
            if(event.getEntityLiving().level.isClientSide())
            {
            }
            else
            {
                LazyOptional<IMorphCapability> cap = player.getCapability(MorphCapabilityAttacher.MORPH_CAP);

                if(cap.isPresent())
                {
                    IMorphCapability resolved = cap.resolve().get();

                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerHurtEvent(LivingAttackEvent event)
    {
        if(event.getEntityLiving() instanceof Player player)
        {
            LazyOptional<IMorphCapability> cap = player.getCapability(MorphCapabilityAttacher.MORPH_CAP);

            if(cap.isPresent())
            {
                IMorphCapability resolved = cap.resolve().get();

                // We shouldn't take any damage from slimes if they are not aggroed on us
                if(resolved.getCurrentMorph().isPresent() && event.getSource().getEntity() != null && event.getSource().getEntity() instanceof Slime slime
                        && slime.getTarget() != player && (event.getEntity().getServer().getTickCount() - resolved.getLastAggroTimestamp()) > resolved.getLastAggroDuration())
                {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTakingDamage(LivingDamageEvent event)
    {
        // Check if living is a Mob and therefore "evil"
        if(event.getSource().getEntity() instanceof Player source && event.getEntityLiving() instanceof Enemy && !event.getEntity().level.isClientSide)
        {

            LazyOptional<IMorphCapability> cap = source.getCapability(MorphCapabilityAttacher.MORPH_CAP);
            aggro(cap.resolve().get(), event.getEntity().getServer().getGameRules().getInt(Static.MORPH_AGGRO_DURATION));
        }
    }

    @SubscribeEvent
    public static void onChangedPose(TickEvent.PlayerTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            MorphUtil.processCap(event.player, cap ->
            {
                if(cap.getCurrentMorph().isPresent())
                {
                    if((event.player.getBoundingBox().maxY - event.player.getBoundingBox().minY) < 1 && event.player.getPose() == Pose.SWIMMING && !event.player.isSwimming())
                    {
                        event.player.setPose(Pose.STANDING);
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void onMorphedClient(PlayerMorphEvent.Client.Post event)
    {
        event.getPlayer().refreshDimensions();
    }

    @SubscribeEvent
    public static void onMorphedServer(PlayerMorphEvent.Server.Post event)
    {
        event.getPlayer().refreshDimensions();

        if(event.getAboutToMorphTo() == null)
            return;

    }

    @SubscribeEvent
    public static void onMorphingServer(PlayerMorphEvent.Server.Pre event)
    {
        if(event.getAboutToMorphTo() != null && event.getAboutToMorphTo().isDisabled())
        {
            event.setCanceled(true);
            return;
        }

        event.getMorphCapability().getCurrentMorph().ifPresent(currentMorph -> {});
    }

    private static void aggro(IMorphCapability capability, int aggroDuration)
    {
        capability.setLastAggroTimestamp(ServerLifecycleHooks.getCurrentServer().getTickCount());
        capability.setLastAggroDuration(aggroDuration);
    }

    @SubscribeEvent
    public static void onTargetBeingSet(LivingSetAttackTargetEvent event)
    {
        if(event.getEntity().level.isClientSide())
            return;

        // This iron golem exception is needed because we don't want players morphed as zombies to sneak around iron golems
        if(event.getEntityLiving() instanceof Mob aggressor && event.getTarget() instanceof Player player && event.getTarget() != event.getEntityLiving().getLastHurtByMob() && !(event.getEntity() instanceof IronGolem || event.getEntity() instanceof EnderMan))
        {

            LazyOptional<IMorphCapability> cap = player.getCapability(MorphCapabilityAttacher.MORPH_CAP);

            if(cap.isPresent())
            {
                IMorphCapability resolved = cap.resolve().get();

                if(resolved.getCurrentMorph().isPresent())
                {
                    if(!resolved.shouldMobsAttack() && (event.getEntity().getServer().getTickCount() - resolved.getLastAggroTimestamp()) > resolved.getLastAggroDuration())
                        aggressor.setTarget(null);
                    else
                    {
                        aggro(resolved, event.getEntity().getServer().getGameRules().getInt(Static.MORPH_AGGRO_DURATION));
                    }
                }
                // Do this so that we can't morph to player, wait the 10 sec, and move back.
                else
                    aggro(resolved, event.getEntity().getServer().getGameRules().getInt(Static.MORPH_AGGRO_DURATION));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onCalculatingAABB(EntityEvent.Size event)
    {
        if(event.getEntity() instanceof Player)
        {
            Player player = (Player) event.getEntity();
            LazyOptional<IMorphCapability> cap = player.getCapability(MorphCapabilityAttacher.MORPH_CAP);

            if(cap.isPresent())
            {
                float divisor = ShrinkAPIInteractor.getInteractor().getShrinkingValue(player);

                IMorphCapability resolved = cap.resolve().get();
                resolved.getCurrentMorph().ifPresent(item ->
                {
                    try
                    {
                        Entity createdEntity = item.createEntity(event.getEntity().level);
                        createdEntity.setPose(event.getPose());

                        // We do this as we apply our own sneaking logic as I couldn't figure out how to get the multiplier for the eye height... F in the chat plz
                        EntityDimensions newSize = createdEntity.getDimensions(Pose.STANDING);

                        if(ShrinkAPIInteractor.getInteractor().isShrunk(player))
                        {
                            newSize = newSize.scale(1.6f / divisor, 1 / divisor);
                        }

                        if(event.getPose() == Pose.CROUCHING)
                            newSize = newSize.scale(1, .85f);

                        event.setNewSize(newSize, false);
                        //event.setNewEyeHeight(createdEntity.getEyeHeightAccess(event.getPose(), newSize));
                        event.setNewEyeHeight(newSize.height * 0.85f);
                        //event.setNewEyeHeight(player.getEyeHeightAccess(event.getPose(), createdEntity.getSize(event.getPose())));
                    }
                    catch(NullPointerException ex)
                    {
                        LOGGER.catching(ex);

                        if(!player.level.isClientSide)
                            MorphUtil.morphToServer(Optional.empty(), Optional.empty(), player);
                        else
                        {
                            resolved.demorph();
                            player.sendMessage(new TextComponent(ChatFormatting.RED + "Couldn't morph to " + item.getEntityType().getRegistryName().toString() + ". This is a compatability issue. If possible, report this to the mod author on GitHub."), new UUID(0, 0));
                        }
                    }
                });
            }
        }
    }
}
