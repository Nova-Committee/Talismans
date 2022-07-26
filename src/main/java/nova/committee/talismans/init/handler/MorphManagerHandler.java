package nova.committee.talismans.init.handler;

import net.minecraft.world.entity.Entity;
import nova.committee.talismans.common.morph.FallbackMorphManager;
import nova.committee.talismans.common.morph.IMorphManager;
import nova.committee.talismans.common.morph.MorphItem;

import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/25 15:37
 * Version: 1.0
 */
public class MorphManagerHandler {
    public static final FallbackMorphManager FALLBACK = new FallbackMorphManager();

    private static ArrayList<IMorphManager<?, ?>> morphManagers = new ArrayList<>();

    public static void registerDefaultManagers()
    {
        registerMorphManager(FALLBACK);
    }

    public static void registerMorphManager(IMorphManager<?, ?> manager)
    {
        morphManagers.add(manager);
    }

    @Nullable
    /** This method returns null if a morph item could not be created from a dead entity. **/
    public static MorphItem createMorphFromDeadEntity(Entity killedEntity)
    {
        for(int i = morphManagers.size() - 1; i >= 0; i--)
        {
            IMorphManager<?, ?> manager = morphManagers.get(i);

            if(manager.doesManagerApplyTo(killedEntity.getType()))
            {
                return manager.createMorphFromEntity(killedEntity);
            }
        }

        return null;
    }
}
