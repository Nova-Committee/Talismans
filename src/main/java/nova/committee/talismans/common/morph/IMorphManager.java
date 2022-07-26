package nova.committee.talismans.common.morph;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/25 15:29
 * Version: 1.0
 */
public interface IMorphManager<T extends MorphItem, D>
{
    boolean doesManagerApplyTo(EntityType<?> type);

    T createMorphFromEntity(Entity entity);

    T createMorph(EntityType<?> entity, CompoundTag nbt, D data, boolean forceNBT);

    default T createMorph(EntityType<?> entity, CompoundTag nbt, D data)
    {
        return createMorph(entity, nbt, data, false);
    }

    T createMorph(EntityType<?> entity, D data);

    boolean equalsFor(T item1, T item2);
    int hashCodeFor(T item);
}
