package nova.committee.talismans.init.registry;

import cn.evolvefield.mods.atomlib.utils.UserUtil;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/27 18:28
 * Version: 1.0
 */
public class ModFakePlayer extends FakePlayer {

    private static WeakReference<ModFakePlayer> INSTANCE;

    /**
     * UUID of a player we are pretending to be, null to use the default Mek one
     */
    private UUID emulatingUUID = null;

    private ModFakePlayer(ServerLevel world) {
        super(world, new FakeGameProfile());
        ((FakeGameProfile) this.getGameProfile()).myFakePlayer = this;
    }

    @Override
    public boolean canBeAffected(@Nonnull MobEffectInstance effect) {
        return false;
    }

    public void setEmulatingUUID(UUID uuid) {
        this.emulatingUUID = uuid;
    }

    @Nonnull
    @Override
    public UUID getUUID() {
        return this.emulatingUUID == null ? super.getUUID() : this.emulatingUUID;
    }

    @Override
    public Vec3 position() {
        //Provide the actual position that forge's fake player hides in this method
        return new Vec3(getX(), getY(), getZ());
    }

    @Override
    public BlockPos blockPosition() {
        //Provide the actual block position that forge's fake player hides in this method
        return new BlockPos(getBlockX(), getBlockY(), getBlockZ());
    }

    /**
     * Acquire a Fake Player and call a function which makes use of the player. Afterwards, the Fake Player's world is nulled out to prevent GC issues. Emulated UUID is
     * also reset.
     *
     * Do NOT store a reference to the Fake Player, so that it may be Garbage Collected. A fake player _should_ only need to be short-lived
     *
     * @param world              World to set on the fake player
     * @param fakePlayerConsumer consumer of the fake player
     * @param <R>                Result of a computation, etc
     *
     * @return the return value of fakePlayerConsumer
     */
    @SuppressWarnings("WeakerAccess")
    public static <R> R withFakePlayer(ServerLevel world, Function<ModFakePlayer, R> fakePlayerConsumer) {
        ModFakePlayer actual = INSTANCE == null ? null : INSTANCE.get();
        if (actual == null) {
            actual = new ModFakePlayer(world);
            INSTANCE = new WeakReference<>(actual);
        }
        ModFakePlayer player = actual;
        player.level = world;
        R result = fakePlayerConsumer.apply(player);
        player.emulatingUUID = null;
        player.level = null;//don't keep reference to the World
        return result;
    }

    /**
     * Same as {@link ModFakePlayer#withFakePlayer(ServerLevel, java.util.function.Function)} but sets the Fake Player's position. Use when you think the entity position
     * is relevant.
     *
     * @param world              World to set on the fake player
     * @param fakePlayerConsumer consumer of the fake player
     * @param x                  X pos to set
     * @param y                  Y pos to set
     * @param z                  Z pos to set
     * @param <R>                Result of a computation, etc
     *
     * @return the return value of fakePlayerConsumer
     */
    public static <R> R withFakePlayer(ServerLevel world, double x, double y, double z, Function<ModFakePlayer, R> fakePlayerConsumer) {
        return withFakePlayer(world, fakePlayer -> {
            fakePlayer.setPosRaw(x, y, z);
            return fakePlayerConsumer.apply(fakePlayer);
        });
    }

    public static void releaseInstance(LevelAccessor world) {
        // If the fake player has a reference to the world getting unloaded,
        // null out the fake player so that the world can unload
        ModFakePlayer actual = INSTANCE == null ? null : INSTANCE.get();
        if (actual != null && actual.level == world) {
            actual.level = null;
        }
    }

    /**
     * Game profile supporting our UUID emulation
     */
    private static class FakeGameProfile extends GameProfile {

        private ModFakePlayer myFakePlayer = null;

        public FakeGameProfile() {
            super(UserUtil.gameProfile.getId(), UserUtil.gameProfile.getName());
        }

        private UUID getEmulatingUUID() {
            return myFakePlayer == null ? null : myFakePlayer.emulatingUUID;
        }

        @Override
        public UUID getId() {
            UUID emulatingUUID = getEmulatingUUID();
            return emulatingUUID == null ? super.getId() : emulatingUUID;
        }

        @Override
        public String getName() {
            UUID emulatingUUID = getEmulatingUUID();
            return emulatingUUID == null ? super.getName() : UserUtil.getLastKnownUsername(emulatingUUID);
        }

        //NB: super check they're the same class, we only check that name & id match
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof GameProfile that)) {
                return false;
            }
            return Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName());
        }

        @Override
        public int hashCode() {
            UUID id = getId();
            String name = getName();
            int result = id == null ? 0 : id.hashCode();
            result = 31 * result + (name == null ? 0 : name.hashCode());
            return result;
        }
    }
}
