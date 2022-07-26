package nova.committee.talismans.init.handler;

import net.minecraft.world.entity.Entity;
import nova.committee.talismans.client.morph.handler.IEntitySynchronizer;

import java.util.ArrayList;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/26 12:23
 * Version: 1.0
 */
public class EntitySynchronizerHandler {
    private static ArrayList<IEntitySynchronizer> synchronizers = new ArrayList<>();

    public static synchronized void addEntitySynchronizer(IEntitySynchronizer synchronizer)
    {
        synchronizers.add(synchronizer);
    }

    public static ArrayList<IEntitySynchronizer> getSynchronizers()
    {
        return synchronizers;
    }

    public static ArrayList<IEntitySynchronizer> getSynchronizersForEntity(Entity entity)
    {
        ArrayList<IEntitySynchronizer> syncs = new ArrayList<>();

        // Create a list based on all the synchronizers that match the entity.
        for(IEntitySynchronizer sync : EntitySynchronizerHandler.getSynchronizers())
        {
            if(sync.appliesToMorph(entity))
                syncs.add(sync);
        }

        return syncs;
    }
}
