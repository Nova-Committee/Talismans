package nova.committee.talismans.common.morph.nbt;

import net.minecraft.nbt.CompoundTag;
import nova.committee.talismans.common.morph.FallbackMorphItem;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/25 15:39
 * Version: 1.0
 */
public class JsonMorphNBTHandler implements IMorphNBTHandler{
    private NBTPath[] trackedNbt;
    private CompoundTag defaultNbt;

    public JsonMorphNBTHandler(CompoundTag defaultNbt, NBTPath[] trackedNbt)
    {
        this.defaultNbt = defaultNbt;
        this.trackedNbt = trackedNbt;
    }

    @Override
    public boolean areEquals(FallbackMorphItem item1, FallbackMorphItem item2)
    {
        CompoundTag fallback1Serialized = item1.serializeAdditional();
        CompoundTag fallback2Serialized = item2.serializeAdditional();

        // This better work...
        return fallback1Serialized.equals(fallback2Serialized);
    }

    @Override
    public int getHashCodeFor(FallbackMorphItem item)
    {
        int hashCode = item.getEntityType().getRegistryName().toString().hashCode();
        CompoundTag nbt = item.serializeAdditional();

        // Let's just hope this works...
        return nbt.hashCode() ^ hashCode;
    }

    @Override
    public CompoundTag applyDefaultNBTData(CompoundTag in)
    {
        CompoundTag out = defaultNbt.copy();

        // Copy every path from the in compound nbt to the out compound nbt
        for(NBTPath nbtPath : trackedNbt)
            nbtPath.copyTo(in, out);

        return out;
    }

    public CompoundTag getDefaultNbt()
    {
        return defaultNbt;
    }

    public NBTPath[] getTrackedNbt()
    {
        return trackedNbt;
    }
}
