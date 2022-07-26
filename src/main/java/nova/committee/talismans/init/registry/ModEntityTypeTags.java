package nova.committee.talismans.init.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import nova.committee.talismans.Static;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/26 16:48
 * Version: 1.0
 */
public class ModEntityTypeTags {
    public static TagKey<EntityType<?>> DISABLE_SNEAK_TRANSFORM = createTag("disable_sneak_transform");
    public static TagKey<EntityType<?>> IRON_GOLEM_ALIKE = createTag("iron_golem_alike");
    public static TagKey<EntityType<?>> UNDEAD = createTag("undead");

    public static TagKey<EntityType<?>> createTag(ResourceLocation rl)
    {
        return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, rl);
    }

    public static TagKey<EntityType<?>> createTag(String name)
    {
        return createTag(new ResourceLocation(Static.MOD_ID, name));
    }
}
