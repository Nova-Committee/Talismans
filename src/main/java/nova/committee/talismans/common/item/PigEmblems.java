package nova.committee.talismans.common.item;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import nova.committee.talismans.client.sound.LaserLoopSound;
import nova.committee.talismans.common.laser.particle.LaserParticleData;
import nova.committee.talismans.common.net.LaserHitBlockPacket;
import nova.committee.talismans.init.handler.NetworkHandler;
import nova.committee.talismans.init.registry.ModDamageSource;
import nova.committee.talismans.init.registry.ModFakePlayer;
import nova.committee.talismans.init.registry.ModSounds;
import nova.committee.talismans.util.FloatingLong;
import nova.committee.talismans.util.Pos3D;
import nova.committee.talismans.util.VectorUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Comparator;
import java.util.List;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/22 19:10
 * Version: 1.0
 */
@ParametersAreNonnullByDefault
public class PigEmblems extends BaseEmblems {
    int reachDistance = 50;
    private BlockPos digging;
    private float diggingProgress = 0;
    private LaserLoopSound laserLoopSound;

    public PigEmblems() {
        setRegistryName("pig_em");
    }


    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return UseAnim.CROSSBOW;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }
    @Override
    public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
        return true;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player pPlayer, InteractionHand hand) {
        var stack = pPlayer.getItemInHand(hand);
        if (level.isClientSide) {
            pPlayer.playSound(ModSounds.laser_start, 0.5f, 1f);
            return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        }
        pPlayer.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @OnlyIn(Dist.CLIENT)
    public void playLoopSound(LivingEntity player, ItemStack stack) {
        Player myplayer = Minecraft.getInstance().player;
        if (myplayer.equals(player)) {
            if (laserLoopSound == null) {
                laserLoopSound = new LaserLoopSound((Player) player, 0.5f);
                Minecraft.getInstance().getSoundManager().play(laserLoopSound);
            }
        }
    }


    @Override
    public void onUsingTick(ItemStack stack, LivingEntity entity1, int count) {
        var world = entity1.getCommandSenderWorld();
        if (world.isClientSide) {
            this.playLoopSound(entity1, stack);
        }
        var player = (ServerPlayer) entity1;
        var direction = player.getDirection();
        double range = 64;
        Vec3 look = player.getLookAngle();
        Vec3 from = new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());

        Vec3 to = new Vec3(player.getX() + look.x * range, player.getY() + player.getEyeHeight() + look.y * range, player.getZ() + look.z * range);
        sendLaserDataToPlayers(world, new LaserParticleData(direction, to.distanceTo(from)), from);
//            Pos3D from = new Pos3D(player.position()).translate(direction, 0.501);
//            Pos3D to = from.translate(direction, 64 - 0.002);
//        BlockHitResult result = world.clip(new ClipContext(from, to, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, null));
//        if (result.getType() != HitResult.Type.MISS) {
//            to = result.getLocation();
//        }
//        List<Entity> hitEntities = world.getEntitiesOfClass(Entity.class, VectorUtil.getAABB(from, to));
//        if (hitEntities.isEmpty()) {
//            // todo 改变红石状态为false
//        } else {
//            // todo 改变红石状态为true
//            Vec3 finalFrom = from;
//            hitEntities.sort(Comparator.comparing(entity -> entity.distanceToSqr(finalFrom)));
//            for (Entity entity : hitEntities) {
//                if (entity.isInvulnerableTo(ModDamageSource.LASER)) {
//                    //更新激光要去的位置
//                    to = new Vec3(player.getX() + entity.getX(), player.getY() + player.getEyeHeight() + entity.getY(), player.getZ() + entity.getZ());
//                    break;
//                }
//                if (entity instanceof ItemEntity item && handleHitItem(item)) {
//                    //TODO: 推动特定的物品
//                    continue;
//                }
//                float damage = 10;
//                float health = 0;
//                if (entity instanceof LivingEntity livingEntity) {
//                    if (livingEntity.isBlocking() && livingEntity.getUseItem().canPerformAction(ToolActions.SHIELD_BLOCK)) {
//                        Vec3 viewVector = livingEntity.getViewVector(1);
//                        Vec3 vectorTo = from.vectorTo(livingEntity.position()).normalize();
//                        vectorTo = new Vec3(vectorTo.x, 0, vectorTo.z);
//                        //检验玩家是否面对激光
//                        if (vectorTo.dot(viewVector) < 0) {
//                            float damageBlocked = damageShield(livingEntity, livingEntity.getUseItem(), damage, 2);
//                            //如果盾牌还有耐久
//                            if (damageBlocked > 0) {
//                                //如果将伤害全部吸收，则更新激光位置，设置为中断
//                                to = new Vec3(player.getX() + entity.getX(), player.getY() + player.getEyeHeight() + entity.getY(), player.getZ() + entity.getZ());
//                                break;
//                            }
//                        }
//                    }
//                    health = livingEntity.getHealth();
//                }
//                if (!entity.fireImmune()) {
//                    //设置燃烧状态
//                    entity.setSecondsOnFire(40);
//                }
//                int lastHurtResistTime = entity.invulnerableTime;
//                //取消无敌时间
//                entity.invulnerableTime = 0;
//                boolean damaged = entity.hurt(ModDamageSource.LASER, damage);
//                //取消无时间间隔卡顿
//                entity.invulnerableTime = lastHurtResistTime;
//                if (damaged) {
//                    //如果造成了伤害
//                    if (entity instanceof LivingEntity livingEntity) {
//                        //更新伤害
//                        damage = Math.min(damage, Math.max(0, health - livingEntity.getHealth()));
//                    }
//
//                    to = new Vec3(player.getX() + entity.getX(), player.getY() + player.getEyeHeight() + entity.getY(), player.getZ() + entity.getZ());
//                    break;
//
//                }
//
//            }
//        }

//        if (result.getType() == HitResult.Type.MISS) {
//            digging = null;
//            diggingProgress = 0;
//        } else {
//            BlockPos hitPos = result.getBlockPos();
//            if (!hitPos.equals(digging)) {
//                digging = result.getType() == HitResult.Type.MISS ? null : hitPos;
//                diggingProgress = 0;
//            }
//
//            //否则，添加破坏方块的进度
//            var hitState = world.getBlockState(hitPos);
//            float hardness = hitState.getDestroySpeed(world, hitPos);
//            if (hardness >= 0) {
//                diggingProgress = diggingProgress + 2;
//                if (diggingProgress < 10) {
//                    ModFakePlayer.withFakePlayer((ServerLevel) world, to.x(), to.y(), to.z(), dummy -> {
//                        dummy.setEmulatingUUID(player.getUUID());//假装是玩家
//                        BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, hitPos, hitState, dummy);
//                        if (!MinecraftForge.EVENT_BUS.post(event)) {
//                            handleBreakBlock(world, hitState, hitPos);
//                            hitState.onRemove(world, hitPos, Blocks.AIR.defaultBlockState(), false);
//                            world.removeBlock(hitPos, false);
//                            world.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, hitPos, Block.getId(hitState));
//                        }
//                        return dummy;
//                    });
//
//                } else {
//                    if (world instanceof ServerLevel level) {
//                        level.getChunkSource().chunkMap.getPlayers(new ChunkPos(player.getOnPos()), false).forEach(p -> NetworkHandler.INSTANCE.sendTo(new LaserHitBlockPacket(result), player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT));
//                    } else {
//                        NetworkHandler.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunk(player.getOnPos().getX() >> 4, player.getOnPos().getZ() >> 4)), new LaserHitBlockPacket(result));
//                    }
//                }
//            }
//
//        }
    }
    protected void handleBreakBlock(Level level, BlockState state, BlockPos hitPos) {
        Block.dropResources(state, level, hitPos, level.getBlockEntity(hitPos));
    }


    protected boolean handleHitItem(ItemEntity entity) {
        ItemStack stack = entity.getItem();

        // todo 对容器造成毁灭
        if (stack.isEmpty()) {
            //如果完成对实体容器内物品的清楚，则移除该物品实体
            entity.discard();
        }
        return true;
    }

    private float damageShield(LivingEntity livingEntity, ItemStack activeStack, float damage, int absorptionRatio) {
        //根据吸收比例吸收伤害
        float damageBlocked = damage;
        float effectiveDamage = damage / absorptionRatio;
        if (effectiveDamage >= 1) {
            //让护盾吸收伤害
            ShieldBlockEvent event = ForgeHooks.onShieldBlock(livingEntity, ModDamageSource.LASER, effectiveDamage);
            if (event.isCanceled()) {
                return 0;
            } else if (event.shieldTakesDamage()) {
                int durabilityNeeded = 1 + Mth.floor(effectiveDamage);
                int activeDurability = activeStack.getMaxDamage() - activeStack.getDamageValue();
                InteractionHand hand = livingEntity.getUsedItemHand();
                activeStack.hurtAndBreak(durabilityNeeded, livingEntity, entity -> {
                    entity.broadcastBreakEvent(hand);
                    if (livingEntity instanceof Player player) {
                        ForgeEventFactory.onPlayerDestroyItem(player, activeStack, hand);
                    }
                });
                if (activeStack.isEmpty()) {
                    if (hand == InteractionHand.MAIN_HAND) {
                        livingEntity.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    } else {
                        livingEntity.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                    }
                    livingEntity.stopUsingItem();
                    livingEntity.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + 0.4F * livingEntity.level.random.nextFloat());
                    //格挡消耗耐久
                    int unblockedDamage = (durabilityNeeded - activeDurability) * absorptionRatio;
                    damageBlocked = Math.max(0, damage - unblockedDamage);
                }
            }
        }
        if (livingEntity instanceof ServerPlayer player && damageBlocked > 0 && damageBlocked < 3.4028235E37F) {
            player.awardStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(damageBlocked * 10F));
        }
        return damageBlocked;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity entityLiving, int pTimeCharged) {
        if (world.isClientSide) {
            if (laserLoopSound != null) {
                if (!laserLoopSound.isStopped()) {
                    entityLiving.playSound(ModSounds.laser_stop, 0.5f, 1f);
                }
                laserLoopSound = null;
            }
        }
        entityLiving.stopUsingItem();
        super.releaseUsing(stack, world, entityLiving, pTimeCharged);
    }



    private void sendLaserDataToPlayers(Level level, LaserParticleData data, Vec3 from) {
        if (!level.isClientSide && level instanceof ServerLevel serverWorld) {
            for (ServerPlayer player : serverWorld.players()) {
                serverWorld.sendParticles(player, data, true, from.x, from.y, from.z, 1, 0, 0, 0, 0);
            }
        }
    }

    public static ItemStack getPig(Player player) {
        ItemStack heldItem = player.getMainHandItem();
        if (!(heldItem.getItem() instanceof PigEmblems)) {
            heldItem = player.getOffhandItem();
            if (!(heldItem.getItem() instanceof PigEmblems)) {
                return ItemStack.EMPTY;
            }
        }
        return heldItem;
    }

}
