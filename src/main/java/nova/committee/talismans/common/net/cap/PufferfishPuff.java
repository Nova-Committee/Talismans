package nova.committee.talismans.common.net.cap;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import nova.committee.talismans.common.morph.cap.instance.pufferfish.IPufferfishCapability;
import nova.committee.talismans.common.morph.cap.instance.pufferfish.PufferfishCapabilityInstance;

import java.util.function.Supplier;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/26 16:26
 * Version: 1.0
 */
public class PufferfishPuff extends CommonCapabilitySynchronizer<PufferfishPuff.PufferfishPuffPacket, IPufferfishCapability>
{
    public PufferfishPuff()
    {
        super(PufferfishCapabilityInstance.PUFFER_CAP);
    }

    @Override
    public void encodeAdditional(PufferfishPuffPacket packet, FriendlyByteBuf buffer)
    {
        buffer.writeInt(packet.getOriginalDuration());
        buffer.writeInt(packet.getDuration());
    }

    @Override
    public PufferfishPuffPacket decodeAdditional(FriendlyByteBuf buffer)
    {
        return new PufferfishPuffPacket(buffer.readInt(), buffer.readInt());
    }

    @Override
    public boolean handleCapabilitySync(PufferfishPuffPacket packet, Supplier<NetworkEvent.Context> ctx, Player player, IPufferfishCapability capabilityInterface)
    {
        capabilityInterface.setOriginalPuffTime(packet.getOriginalDuration());
        capabilityInterface.setPuffTime(packet.getDuration());

        return true;
    }


    public static class PufferfishPuffPacket extends CommonCapabilitySynchronizer.Packet
    {
        private int originalDuration;
        private int duration;



        public PufferfishPuffPacket(int originalDuration, int duration)
        {
            this.originalDuration = originalDuration;
            this.duration = duration;
        }

        public PufferfishPuffPacket() {

        }

        public int getDuration()
        {
            return duration;
        }

        public int getOriginalDuration()
        {
            return originalDuration;
        }

        public void setDuration(int duration)
        {
            this.duration = duration;
        }

        public void setOriginalDuration(int originalDuration)
        {
            this.originalDuration = originalDuration;
        }
    }
}
