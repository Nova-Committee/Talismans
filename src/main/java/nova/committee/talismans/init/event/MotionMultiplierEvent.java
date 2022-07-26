package nova.committee.talismans.init.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.Event;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/26 15:10
 * Version: 1.0
 */
public class MotionMultiplierEvent extends Event
{
    private final Vec3 oldMotionMultiplier;
    private Vec3 newMotionMultiplier;
    private final Entity entity;
    private final BlockState blockState;
    private final Level world;
    private boolean dirty = false;

    public MotionMultiplierEvent(Vec3 oldMotionMultiplier, Entity entity, BlockState blockState, Level world)
    {
        this.oldMotionMultiplier = oldMotionMultiplier;
        this.newMotionMultiplier = oldMotionMultiplier;
        this.entity = entity;
        this.blockState = blockState;
        this.world = world;
    }

    /** Returns the entity whose motion multiplier setter was called. **/
    public Entity getEntity()
    {
        return entity;
    }

    /**
     * Gets the block state of the block that caused the call of
     * collision. Note that the actual block might have changed since then.
     **/
    public BlockState getBlockState()
    {
        return blockState;
    }

    /** Returns the world in which the collision happened. **/
    public Level getWorld()
    {
        return world;
    }

    /**
     * Returns the original motion multiplier that would be used if nothing would be
     * modified.
     **/
    public final Vec3 getOriginalMotionMultiplier()
    {
        return oldMotionMultiplier;
    }


    public Vec3 getNewMotionMultiplier()
    {
        return new Vec3(newMotionMultiplier.x, newMotionMultiplier.y, newMotionMultiplier.z);
    }

    /** Sets the new motion multiplier and marks this motion multiplier event as dirty. **/
    public void setNewMotionMultiplier(Vec3 newWebSpeed)
    {
        this.newMotionMultiplier = newWebSpeed;
        this.dirty = true;
    }

    /**
     * Returns whether this motion multiplier event is dirty or not. If it is dirty, we replace
     * the vanilla logic with our logic, if, however, this is not dirty, we just
     * pretend that this event didn't happen and ignore it.
     **/
    public boolean isDirty()
    {
        return dirty;
    }

    @Override
    public boolean isCancelable()
    {
        return true;
    }
}
