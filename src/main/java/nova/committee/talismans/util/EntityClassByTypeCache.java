package nova.committee.talismans.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.HashMap;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/26 13:52
 * Version: 1.0
 */
public class EntityClassByTypeCache {
    private static HashMap<EntityType<?>, Class<? extends Entity>> cache = new HashMap<>();

    /**
     * Ugly fix for entity types not containing their class types. Hopefully, this
     * doesn't create crashes lul.
     **/
    @SuppressWarnings("unchecked")
    public static <T extends Entity> Class<T> getClassForEntityType(EntityType<T> entity)
    {
        Class<T> clazz = (Class<T>) cache.get(entity);

        if(clazz == null)
        {
            T entityInstance = entity.create(ServerLifecycleHooks.getCurrentServer().getAllLevels().iterator().next());
            clazz = (Class<T>) entityInstance.getClass();
            cache.put(entity, clazz);
        }

        return clazz;
    }
}
