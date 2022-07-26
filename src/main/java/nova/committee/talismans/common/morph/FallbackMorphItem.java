package nova.committee.talismans.common.morph;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import nova.committee.talismans.init.handler.MorphManagerHandler;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/25 15:36
 * Version: 1.0
 */
public class FallbackMorphItem extends MorphItem{
    private CompoundTag entityData;
    private EntityType<?> entityType;

    public FallbackMorphItem(CompoundTag entityData, EntityType<?> entityType)
    {
        super("fallback_morph_item");
        this.entityData = entityData;
        this.entityType = entityType;
        entityData.putString("id", entityType.getRegistryName().toString());
    }

    public FallbackMorphItem(EntityType<?> entityType)
    {
        this(new CompoundTag(), entityType);
    }

    public FallbackMorphItem()
    {
        super("fallback_morph_item");
    }

    @Override
    public EntityType<?> getEntityType()
    {
        return entityType;
    }

    @Override
    public Entity createEntity(Level world)
    {
        Entity entityLoaded = EntityType.loadEntityRecursive(entityData, world, entity -> entity);

        if(entityLoaded == null)
            throw new NullPointerException("The morph item \"" + entityType.getRegistryName().toString() + "\" could *NOT* be initialized, thus being null now.");

        return entityLoaded;
    }

    @Override
    public void deserializeAdditional(CompoundTag nbt)
    {
        this.entityType = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(nbt.getString("id")));
        this.entityData = nbt;
    }

    @Override
    public CompoundTag serializeAdditional()
    {
        return entityData;
    }

    @Override
    public int hashCode()
    {
        return MorphManagerHandler.FALLBACK.hashCodeFor(this);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof FallbackMorphItem casted)
        {
            return MorphManagerHandler.FALLBACK.equalsFor(this, casted);
        }

        return false;
    }
}
