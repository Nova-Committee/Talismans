package nova.committee.talismans.init.registry;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import nova.committee.talismans.Static;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/27 17:45
 * Version: 1.0
 */
@MethodsReturnNonnullByDefault
public class ModDamageSource extends DamageSource {
    public static final ModDamageSource LASER = new ModDamageSource("laser");
    private final String translationKey;

    private final Vec3 damageLocation;

    public ModDamageSource(String damageType) {
        this(damageType, null);
    }

    private ModDamageSource(@Nonnull String damageType, @Nullable Vec3 damageLocation) {
        super(Static.MOD_ID + "." + damageType);
        this.translationKey = "death.attack." + getMsgId();
        this.damageLocation = damageLocation;
    }

    public ModDamageSource fromPosition(@Nonnull Vec3 damageLocation) {
        return new ModDamageSource(getMsgId(), damageLocation);
    }

    public String getTranslationKey() {
        return translationKey;
    }

    @Nullable
    @Override
    public Vec3 getSourcePosition() {
        return damageLocation;
    }

    @Override
    public ModDamageSource setProjectile() {
        super.setProjectile();
        return this;
    }

    @Override
    public ModDamageSource setExplosion() {
        super.setExplosion();
        return this;
    }

    @Override
    public ModDamageSource bypassArmor() {
        super.bypassArmor();
        return this;
    }

    @Override
    public ModDamageSource bypassInvul() {
        super.bypassInvul();
        return this;
    }

    @Override
    public ModDamageSource bypassMagic() {
        super.bypassMagic();
        return this;
    }

    @Override
    public ModDamageSource setIsFire() {
        super.setIsFire();
        return this;
    }

    @Override
    public ModDamageSource setScalesWithDifficulty() {
        super.setScalesWithDifficulty();
        return this;
    }

    @Override
    public ModDamageSource setMagic() {
        super.setMagic();
        return this;
    }
}
