package nova.committee.talismans.common.net;

import cn.evolvefield.mods.atomlib.common.net.IPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/7/27 18:36
 * Version: 1.0
 */
public class LaserHitBlockPacket extends IPacket<LaserHitBlockPacket> {
    private BlockHitResult result;

    public LaserHitBlockPacket(){

    }
    public LaserHitBlockPacket(BlockHitResult result) {
        this.result = result;
    }
    @Override
    public LaserHitBlockPacket read(FriendlyByteBuf buf) {
        return new LaserHitBlockPacket(buf.readBlockHitResult());
    }

    @Override
    public void write(LaserHitBlockPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockHitResult(result);

    }

    @Override
    public void run(LaserHitBlockPacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (Minecraft.getInstance().level != null) {
            Minecraft.getInstance().particleEngine.addBlockHitEffects(result.getBlockPos(), result);
        }
    }
}
