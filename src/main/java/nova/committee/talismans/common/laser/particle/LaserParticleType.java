package nova.committee.talismans.common.laser.particle;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;

import javax.annotation.Nonnull;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/27 14:29
 * Version: 1.0
 */
public class LaserParticleType extends ParticleType<LaserParticleData> {

    public LaserParticleType() {
        super(false, LaserParticleData.DESERIALIZER);
        setRegistryName("laser");
    }

    @Nonnull
    @Override
    public Codec<LaserParticleData> codec() {
        return LaserParticleData.CODEC;
    }
}
