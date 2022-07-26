package nova.committee.talismans.init.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Event;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/25 15:46
 * Version: 1.0
 */
public class MorphCreatedFromEntityEvent extends Event {
    private CompoundTag tagIn;
    private CompoundTag tagOut;
    private Entity entity;

    public MorphCreatedFromEntityEvent(CompoundTag tagIn, CompoundTag tagOut, Entity entity)
    {
        this.tagIn = tagIn;
        this.tagOut = tagOut;
        this.entity = entity;
    }

    public void setTagOut(CompoundTag tagOut)
    {
        this.tagOut = tagOut;
    }

    public CompoundTag getTagIn()
    {
        return tagIn;
    }

    public CompoundTag getTagOut()
    {
        return tagOut;
    }

    public Entity getEntity()
    {
        return entity;
    }
}
